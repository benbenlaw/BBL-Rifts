package com.benbenlaw.rifts.item;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.entity.EpochopolisEntities;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RiftsItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Rifts.MOD_ID);

    public static final DeferredItem<Item> DISPLACER = ITEMS.registerItem("displacer", DisplacerItem::new);

    public static final DeferredItem<Item> RIFT_STEEL_INGOT = ITEMS.registerItem("rift_steel_ingot", Item::new);
    public static final DeferredItem<Item> RIFT_STEEL_NUGGET = ITEMS.registerItem("rift_steel_nugget", Item::new);

    public static final DeferredItem<Item> RIFT_ELEMENTAL_SPAWN_EGG = ITEMS.registerItem("rift_elemental_spawn_egg",
            properties -> new SpawnEggItem(properties.spawnEgg(EpochopolisEntities.RIFT_ELEMENTAL.get())));

    public static final DeferredItem<Item> RIFT_WRENCH = ITEMS.registerItem("rift_wrench",
            properties -> new RiftWrenchItem(properties.stacksTo(1)));

    public static final DeferredItem<Item> RIFT_SCANNER = ITEMS.registerItem("rift_scanner",
            properties -> new RiftScannerItem(properties.stacksTo(1)));
}
