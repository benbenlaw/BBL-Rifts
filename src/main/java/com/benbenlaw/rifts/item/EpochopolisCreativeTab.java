package com.benbenlaw.rifts.item;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EpochopolisCreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Rifts.MOD_ID);

    public static final Supplier<CreativeModeTab> RIFTS_TAB = CREATIVE_MODE_TABS.register(Rifts.MOD_ID, () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> RiftsBlocks.RIFT_GENERATOR.get().asItem().getDefaultInstance())
            .title(Component.translatable("itemGroup." + Rifts.MOD_ID))
            .displayItems(RiftsItems.ITEMS.getEntries()).build());
}
