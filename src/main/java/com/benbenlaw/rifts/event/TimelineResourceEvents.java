package com.benbenlaw.rifts.event;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.util.EpochopolisTags;
import com.benbenlaw.rifts.world.DepletedResource;
import com.benbenlaw.rifts.world.TimelineResourceData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

@EventBusSubscriber(modid = Rifts.MOD_ID)
public class TimelineResourceEvents {

    @SubscribeEvent
    public static void onBlockBreak(BreakBlockEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;

        BlockState state = event.getState();
        if (state.is(EpochopolisTags.Blocks.SYNCED_ACROSS_TIMELINES)) {
            BlockPos pos = event.getPos();
            Identifier brokenEra = serverLevel.dimension().identifier();

            TimelineResourceData data = TimelineResourceData.get(serverLevel);
            data.markDepleted(pos, Rifts.identifier("synced_resource"), brokenEra);

            for (ServerLevel level : serverLevel.getServer().getAllLevels()) {
                if (level == serverLevel) continue;
                if (level.hasChunk(pos.getX() >> 4, pos.getZ() >> 4)) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        LevelChunk chunk = event.getChunk();

        TimelineResourceData data = TimelineResourceData.get(serverLevel);

        int chunkX = chunk.getPos().x();
        int chunkZ = chunk.getPos().z();

        for (DepletedResource resource : data.allDepleted()) {
            BlockPos pos = resource.pos();
            if ((pos.getX() >> 4) == chunkX && (pos.getZ() >> 4) == chunkZ) {
                if (!resource.depletedInEra().equals(serverLevel.dimension().identifier())) {
                    chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), 0);
                    System.out.println("[Chronopolis] Backfilled to air at " + pos);
                }
            }
        }
    }
}