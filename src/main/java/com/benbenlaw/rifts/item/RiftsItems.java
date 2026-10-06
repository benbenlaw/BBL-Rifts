package com.benbenlaw.rifts.item;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.entity.EpochopolisEntities;
import com.benbenlaw.rifts.item.energy.RiftArmorItem;
import com.benbenlaw.rifts.item.energy.RiftAxeItem;
import com.benbenlaw.rifts.item.energy.RiftHoeItem;
import com.benbenlaw.rifts.item.energy.RiftShovelItem;
import com.benbenlaw.rifts.item.energy.RiftToolItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RiftsItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Rifts.MOD_ID);

    public static final DeferredItem<Item> DISPLACER = ITEMS.registerItem("displacer", DisplacerItem::new);

    public static final DeferredItem<Item> RIFT_STEEL_INGOT = ITEMS.registerItem("rift_steel_ingot", Item::new);
    public static final DeferredItem<Item> RIFT_STEEL_NUGGET = ITEMS.registerItem("rift_steel_nugget", Item::new);
    public static final DeferredItem<Item> RIFT_PEARL = ITEMS.registerItem("rift_pearl", Item::new);

    public static final DeferredItem<Item> RIFT_ELEMENTAL_SPAWN_EGG = ITEMS.registerItem("rift_elemental_spawn_egg",
            properties -> new SpawnEggItem(properties.spawnEgg(EpochopolisEntities.RIFT_ELEMENTAL.get())));

    public static final DeferredItem<Item> RIFT_STEEL_SWORD = ITEMS.registerItem("rift_steel_sword",
            properties -> new RiftToolItem(properties.sword(RiftsMaterials.RIFT_STEEL_TOOLS, 3.0F, -2.4F)));

    public static final DeferredItem<Item> RIFT_STEEL_PICKAXE = ITEMS.registerItem("rift_steel_pickaxe",
            properties -> new RiftToolItem(properties.pickaxe(RiftsMaterials.RIFT_STEEL_TOOLS, 1.0F, -2.8F)));

    public static final DeferredItem<Item> RIFT_STEEL_AXE = ITEMS.registerItem("rift_steel_axe",
            properties -> new RiftAxeItem(RiftsMaterials.RIFT_STEEL_TOOLS, 5.0F, -3.0F, properties));

    public static final DeferredItem<Item> RIFT_STEEL_SHOVEL = ITEMS.registerItem("rift_steel_shovel",
            properties -> new RiftShovelItem(RiftsMaterials.RIFT_STEEL_TOOLS, 1.5F, -3.0F, properties));

    public static final DeferredItem<Item> RIFT_STEEL_HOE = ITEMS.registerItem("rift_steel_hoe",
            properties -> new RiftHoeItem(RiftsMaterials.RIFT_STEEL_TOOLS, -3.0F, 0.0F, properties));

    public static final DeferredItem<Item> RIFT_STEEL_SPEAR = ITEMS.registerItem("rift_steel_spear",
            properties -> new RiftToolItem(properties.spear(RiftsMaterials.RIFT_STEEL_TOOLS, 1.05F, 1.075F, 0.5F, 3.0F, 10.0F, 6.5F, 5.1F, 10.0F, 4.6F)));

    public static final DeferredItem<Item> RIFT_STEEL_HELMET = ITEMS.registerItem("rift_steel_helmet",
            properties -> new RiftArmorItem(properties.humanoidArmor(RiftsMaterials.RIFT_STEEL_ARMOR, ArmorType.HELMET)));

    public static final DeferredItem<Item> RIFT_STEEL_CHESTPLATE = ITEMS.registerItem("rift_steel_chestplate",
            properties -> new RiftArmorItem(properties.humanoidArmor(RiftsMaterials.RIFT_STEEL_ARMOR, ArmorType.CHESTPLATE)));

    public static final DeferredItem<Item> RIFT_STEEL_LEGGINGS = ITEMS.registerItem("rift_steel_leggings",
            properties -> new RiftArmorItem(properties.humanoidArmor(RiftsMaterials.RIFT_STEEL_ARMOR, ArmorType.LEGGINGS)));

    public static final DeferredItem<Item> RIFT_STEEL_BOOTS = ITEMS.registerItem("rift_steel_boots",
            properties -> new RiftArmorItem(properties.humanoidArmor(RiftsMaterials.RIFT_STEEL_ARMOR, ArmorType.BOOTS)));

    public static final DeferredItem<Item> RIFT_WRENCH = ITEMS.registerItem("rift_wrench",
            properties -> new Item(properties.stacksTo(1)));

    public static final DeferredItem<Item> RIFT_SCANNER = ITEMS.registerItem("rift_scanner",
            properties -> new RiftScannerItem(properties.stacksTo(1)));
}
