package com.benbenlaw.rifts.item;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.block.RiftsBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.chat.Component;
import com.benbenlaw.rifts.item.energy.RiftEnergyItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RiftsCreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Rifts.MOD_ID);

    public static final Supplier<CreativeModeTab> RIFTS_TAB = CREATIVE_MODE_TABS.register(Rifts.MOD_ID, () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> RiftsBlocks.RIFT_GENERATOR.get().asItem().getDefaultInstance())
            .title(Component.translatable("itemGroup." + Rifts.MOD_ID))
            .displayItems((parameters, output) -> {
                for (var entry : RiftsItems.ITEMS.getEntries()) {
                    if (entry == RiftsItems.DISPLACER) {
                        for (ResourceKey<Level> dimension : loadedDimensions()) {
                            ItemStack displacer = new ItemStack(entry.get());
                            displacer.set(RiftsDataComponents.DISPLACER_DIMENSION.get(), dimension);
                            output.accept(displacer);
                        }
                        continue;
                    }
                    output.accept(new ItemStack(entry.get()));
                    if (entry.get() instanceof RiftEnergyItem energyItem) {
                        ItemStack charged = new ItemStack(entry.get());
                        RiftEnergyItem.setEnergy(charged, energyItem.getMaxEnergy());
                        output.accept(charged);
                    }
                }
            }).build());

    private static Collection<ResourceKey<Level>> loadedDimensions() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) return List.of(Level.OVERWORLD);
        return connection.levels();
    }
}
