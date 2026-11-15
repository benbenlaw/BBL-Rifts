package com.benbenlaw.rifts.event.client;

import com.benbenlaw.core.util.TooltipUtil;
import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlocks;
import com.benbenlaw.rifts.item.RiftsDataComponents;
import com.benbenlaw.rifts.item.RiftPortalGunItem;
import com.benbenlaw.rifts.item.RiftsItems;
import com.benbenlaw.rifts.item.energy.RiftEnergyItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import com.benbenlaw.rifts.block.custom.RiftPylonBlock;
import com.benbenlaw.rifts.block.custom.RiftTickAcceleratorBlock;
import com.benbenlaw.rifts.config.RiftsStartupConfig;
import com.benbenlaw.rifts.entity.RiftsEntities;
import com.benbenlaw.rifts.entity.client.NaturalRiftRenderer;
import com.benbenlaw.rifts.entity.client.RiftElementalRenderer;
import com.benbenlaw.rifts.entity.client.RiftPortalRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.registries.DeferredBlock;

@EventBusSubscriber(modid = Rifts.MOD_ID, value = Dist.CLIENT)
public class RiftsClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RiftsEntities.DISPLACER.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(RiftsEntities.RIFT_ELEMENTAL.get(), RiftElementalRenderer::new);
        event.registerEntityRenderer(RiftsEntities.NATURAL_RIFT.get(), NaturalRiftRenderer::new);
        event.registerEntityRenderer(RiftsEntities.RIFT_PORTAL.get(), RiftPortalRenderer::new);
    }

    @SubscribeEvent
    public static void onTooltipEvent(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        Integer storedEnergy = stack.get(RiftsDataComponents.RIFT_ENERGY.get());
        if (stack.getItem() instanceof RiftEnergyItem energyItem) {
            event.getToolTip().add(Component.translatable("tooltip.rifts.item_energy",
                    String.format("%,d", RiftEnergyItem.getEnergy(stack)), String.format("%,d", energyItem.getMaxEnergy())).withStyle(ChatFormatting.AQUA));
        } else if (storedEnergy != null && storedEnergy > 0) {
            event.getToolTip().add(Component.translatable("tooltip.rifts.stored_energy", String.format("%,d", storedEnergy)).withStyle(ChatFormatting.AQUA));
        }

        ResourceKey<Level> displacerDimension = stack.get(RiftsDataComponents.DISPLACER_DIMENSION.get());
        if (displacerDimension != null) {
            TooltipUtil.addShiftTooltip(stack, event, RiftsItems.DISPLACER.get(), "tooltip.rifts.displacer_dimension",
                    displacerDimension.identifier().toString());
        }

        if (stack.is(RiftsItems.RIFT_PORTAL_GUN.get())) {
            event.getToolTip().add(Component.translatable("tooltip.rifts.portal_gun_colors",
                    Component.translatable("color.minecraft." + RiftPortalGunItem.portalColor(stack, RiftPortalGunItem.FIRST_PORTAL).getName()),
                    Component.translatable("color.minecraft." + RiftPortalGunItem.portalColor(stack, RiftPortalGunItem.SECOND_PORTAL).getName())).withStyle(ChatFormatting.GRAY));
        }
        TooltipUtil.addShiftTooltip(stack, event, RiftsItems.RIFT_PORTAL_GUN.get(), "tooltip.rifts.rift_portal_gun",
                String.format("%,d", RiftsStartupConfig.PORTAL_GUN_ENERGY_PER_SHOT.get()));

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