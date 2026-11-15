package com.benbenlaw.rifts.event;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = Rifts.MOD_ID)
public class RiftBlockEvents {

    @SubscribeEvent
    public static void addSignBlocks(BlockEntityTypeAddBlocksEvent event) {
        event.modify(BlockEntityType.SIGN, RiftsBlocks.RIFT_SIGN.get(), RiftsBlocks.RIFT_WALL_SIGN.get());
        event.modify(BlockEntityType.HANGING_SIGN, RiftsBlocks.RIFT_HANGING_SIGN.get(), RiftsBlocks.RIFT_WALL_HANGING_SIGN.get());
    }

    @SubscribeEvent
    public static void onToolModification(BlockEvent.BlockToolModificationEvent event) {
        if (event.getItemAbility() != ItemAbilities.AXE_STRIP) return;

        BlockState state = event.getState();
        if (state.is(RiftsBlocks.RIFT_LOG.get())) {
            event.setFinalState(RiftsBlocks.STRIPPED_RIFT_LOG.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)));
        } else if (state.is(RiftsBlocks.RIFT_WOOD.get())) {
            event.setFinalState(RiftsBlocks.STRIPPED_RIFT_WOOD.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS)));
        }
    }
}
