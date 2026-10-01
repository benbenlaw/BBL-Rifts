package com.benbenlaw.rifts.block.pipe;

import com.benbenlaw.rifts.block.capability.RiftEnergyHandler;
import com.benbenlaw.rifts.block.capability.RiftsCapabilities;
import com.benbenlaw.rifts.block.custom.RiftPipeBlock;
import com.benbenlaw.rifts.block.entity.RiftPipeBlockEntity;
import com.benbenlaw.rifts.config.RiftsStartupConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RiftPipeNetworks {

    private record Endpoint(BlockPos target, Direction face) {}

    private record Network(List<Endpoint> sources, List<Endpoint> sinks) {}

    private static class LevelPipes {
        final Set<BlockPos> pipes = new HashSet<>();
        final List<Network> networks = new ArrayList<>();
        boolean dirty = true;
    }

    private static final Map<ResourceKey<Level>, LevelPipes> LEVELS = new HashMap<>();

    public static void register(ServerLevel level, BlockPos pos) {
        LevelPipes pipes = LEVELS.computeIfAbsent(level.dimension(), key -> new LevelPipes());
        pipes.pipes.add(pos.immutable());
        pipes.dirty = true;
    }

    public static void unregister(ServerLevel level, BlockPos pos) {
        LevelPipes pipes = LEVELS.get(level.dimension());
        if (pipes != null) {
            pipes.pipes.remove(pos);
            pipes.dirty = true;
        }
    }

    public static void markDirty(Level level) {
        if (level.isClientSide()) return;
        LevelPipes pipes = LEVELS.get(level.dimension());
        if (pipes != null) {
            pipes.dirty = true;
        }
    }

    public static void clear() {
        LEVELS.clear();
    }

    public static void tick(ServerLevel level) {
        LevelPipes pipes = LEVELS.get(level.dimension());
        if (pipes == null || pipes.pipes.isEmpty()) return;

        if (pipes.dirty) {
            rebuild(level, pipes);
        }

        for (Network network : pipes.networks) {
            transfer(level, network, level.getGameTime());
        }
    }

    private static void rebuild(ServerLevel level, LevelPipes pipes) {
        pipes.dirty = false;
        pipes.networks.clear();

        for (BlockPos pos : List.copyOf(pipes.pipes)) {
            refreshConnections(level, pos);
        }

        Set<BlockPos> visited = new HashSet<>();
        for (BlockPos start : pipes.pipes) {
            if (!visited.add(start)) continue;

            List<Endpoint> sources = new ArrayList<>();
            List<Endpoint> sinks = new ArrayList<>();
            ArrayDeque<BlockPos> queue = new ArrayDeque<>();
            queue.add(start);

            while (!queue.isEmpty()) {
                BlockPos pipePos = queue.poll();
                BlockEntity be = level.getBlockEntity(pipePos);
                if (!(be instanceof RiftPipeBlockEntity pipe)) continue;

                for (Direction direction : Direction.values()) {
                    BlockPos neighbour = pipePos.relative(direction);
                    if (pipes.pipes.contains(neighbour)) {
                        if (visited.add(neighbour)) {
                            queue.add(neighbour);
                        }
                        continue;
                    }
                    PipeMode mode = pipe.getMode(direction);
                    if (mode == PipeMode.EXTRACT) {
                        sources.add(new Endpoint(neighbour, direction.getOpposite()));
                    } else if (mode == PipeMode.INSERT) {
                        sinks.add(new Endpoint(neighbour, direction.getOpposite()));
                    }
                }
            }

            if (!sources.isEmpty() && !sinks.isEmpty()) {
                pipes.networks.add(new Network(sources, sinks));
            }
        }
    }

    private static void refreshConnections(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof RiftPipeBlock) || !(level.getBlockEntity(pos) instanceof RiftPipeBlockEntity pipe)) return;

        BlockState newState = state;
        for (Direction direction : Direction.values()) {
            BlockPos neighbour = pos.relative(direction);
            boolean connected;

            if (level.getBlockState(neighbour).getBlock() instanceof RiftPipeBlock) {
                connected = true;
            } else {
                RiftEnergyHandler handler = level.getCapability(RiftsCapabilities.RIFT_ENERGY, neighbour, direction.getOpposite());
                if (handler == null) {
                    pipe.clearMode(direction);
                    connected = false;
                } else {
                    PipeMode mode = pipe.getMode(direction);
                    if (mode == null) {
                        mode = handler.canInsert() ? PipeMode.INSERT : PipeMode.EXTRACT;
                        pipe.setMode(direction, mode);
                    }
                    connected = mode != PipeMode.OFF;
                }
            }
            newState = newState.setValue(RiftPipeBlock.PROPERTY_BY_DIRECTION.get(direction), connected);
        }

        if (newState != state) {
            level.setBlock(pos, newState, Block.UPDATE_CLIENTS);
        }
    }

    private static void transfer(ServerLevel level, Network network, long gameTime) {
        List<RiftEnergyHandler> sources = resolve(level, network.sources(), true);
        List<RiftEnergyHandler> sinks = resolve(level, network.sinks(), false);
        if (sources.isEmpty() || sinks.isEmpty()) return;

        int throughput = RiftsStartupConfig.PIPE_TRANSFER_PER_TICK.get();

        long demand = 0;
        try (Transaction tx = Transaction.openRoot()) {
            for (RiftEnergyHandler sink : sinks) {
                demand += sink.insert(throughput, tx);
            }
        }
        int wanted = (int) Math.min(throughput, demand);
        if (wanted <= 0) return;

        try (Transaction tx = Transaction.openRoot()) {
            int pulled = 0;
            for (RiftEnergyHandler source : sources) {
                pulled += source.extract(wanted - pulled, tx);
                if (pulled >= wanted) break;
            }
            if (pulled <= 0) return;

            int remaining = pulled;
            int offset = (int) (gameTime % sinks.size());
            for (int i = 0; i < sinks.size() && remaining > 0; i++) {
                remaining -= sinks.get((i + offset) % sinks.size()).insert(remaining, tx);
            }
            if (remaining == 0) {
                tx.commit();
            }
        }
    }

    private static List<RiftEnergyHandler> resolve(ServerLevel level, List<Endpoint> endpoints, boolean extracting) {
        List<RiftEnergyHandler> handlers = new ArrayList<>();
        for (Endpoint endpoint : endpoints) {
            RiftEnergyHandler handler = level.getCapability(RiftsCapabilities.RIFT_ENERGY, endpoint.target(), endpoint.face());
            if (handler != null && (extracting ? handler.canExtract() : handler.canInsert())) {
                handlers.add(handler);
            }
        }
        return handlers;
    }
}
