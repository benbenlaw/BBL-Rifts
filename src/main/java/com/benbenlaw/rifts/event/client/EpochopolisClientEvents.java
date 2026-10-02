package com.benbenlaw.rifts.event.client;

import com.benbenlaw.core.util.TooltipUtil;
import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.item.RiftsDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import com.benbenlaw.rifts.block.custom.RiftPylonBlock;
import com.benbenlaw.rifts.block.custom.RiftTickAcceleratorBlock;
import com.benbenlaw.rifts.config.RiftsStartupConfig;
import com.benbenlaw.rifts.entity.EpochopolisEntities;
import com.benbenlaw.rifts.entity.client.RiftElementalRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.registries.DeferredBlock;

@EventBusSubscriber(modid = Rifts.MOD_ID, value = Dist.CLIENT)
public class EpochopolisClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EpochopolisEntities.DISPLACER.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(EpochopolisEntities.RIFT_ELEMENTAL.get(), RiftElementalRenderer::new);
    }

    @SubscribeEvent
    public static void onTooltipEvent(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        Integer storedEnergy = stack.get(RiftsDataComponents.RIFT_ENERGY.get());
        if (storedEnergy != null && storedEnergy > 0) {
            event.getToolTip().add(Component.translatable("tooltip.rifts.stored_energy", String.format("%,d", storedEnergy)).withStyle(ChatFormatting.AQUA));
        }

        addPylonTooltip(stack, event, RiftsBlocks.BASIC_RIFT_PYLON, "tooltip.rifts.basic_rift_pylon");
        addPylonTooltip(stack, event, RiftsBlocks.ADVANCED_RIFT_PYLON, "tooltip.rifts.advanced_rift_pylon");
        addPylonTooltip(stack, event, RiftsBlocks.ELITE_RIFT_PYLON, "tooltip.rifts.elite_rift_pylon");
        addPylonTooltip(stack, event, RiftsBlocks.ULTIMATE_RIFT_PYLON, "tooltip.rifts.ultimate_rift_pylon");

        addAcceleratorTooltip(stack, event, RiftsBlocks.BASIC_TICK_ACCELERATOR, "tooltip.rifts.basic_tick_accelerator");
        addAcceleratorTooltip(stack, event, RiftsBlocks.ADVANCED_TICK_ACCELERATOR, "tooltip.rifts.advanced_tick_accelerator");
        addAcceleratorTooltip(stack, event, RiftsBlocks.ELITE_TICK_ACCELERATOR, "tooltip.rifts.elite_tick_accelerator");
        addAcceleratorTooltip(stack, event, RiftsBlocks.ULTIMATE_TICK_ACCELERATOR, "tooltip.rifts.ultimate_tick_accelerator");
    }

    private static void addAcceleratorTooltip(ItemStack stack, ItemTooltipEvent event, DeferredBlock<Block> block, String key) {
        if (block.get() instanceof RiftTickAcceleratorBlock accelerator) {
            TooltipUtil.addShiftTooltip(stack, event, block.asItem(), key,
                    String.format("%,d", accelerator.getExtraTicks()), String.format("%,d", accelerator.getEnergyPerTick()));
        }
    }

    private static void addPylonTooltip(ItemStack stack, ItemTooltipEvent event, DeferredBlock<Block> block, String key) {
        if (block.get() instanceof RiftPylonBlock pylon) {
            TooltipUtil.addShiftTooltip(stack, event, block.asItem(), key,
                    String.format("%,d", pylon.getCapacity()), String.format("%,d", pylon.getDrawPerSecond()));
        }
    }
}