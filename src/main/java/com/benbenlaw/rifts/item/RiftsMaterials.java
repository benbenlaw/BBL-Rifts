package com.benbenlaw.rifts.item;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.util.EpochopolisTags;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.EnumMap;
import java.util.Map;

public class RiftsMaterials {

    // Durability values are unused: rift energy is spent instead
    public static final ToolMaterial RIFT_STEEL_TOOLS = new ToolMaterial(
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 1800, 8.5F, 3.5F, 15, EpochopolisTags.Items.RIFT_STEEL_TOOL_MATERIALS);

    public static final ResourceKey<EquipmentAsset> RIFT_STEEL_ASSET = ResourceKey.create(EquipmentAssets.ROOT_ID, Rifts.identifier("rift_steel"));

    public static final ArmorMaterial RIFT_STEEL_ARMOR = new ArmorMaterial(
            30, defense(3, 6, 8, 3, 11), 15, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 0.05F,
            EpochopolisTags.Items.REPAIRS_RIFT_STEEL_ARMOR, RIFT_STEEL_ASSET);

    private static Map<ArmorType, Integer> defense(int boots, int leggings, int chestplate, int helmet, int body) {
        Map<ArmorType, Integer> map = new EnumMap<>(ArmorType.class);
        map.put(ArmorType.BOOTS, boots);
        map.put(ArmorType.LEGGINGS, leggings);
        map.put(ArmorType.CHESTPLATE, chestplate);
        map.put(ArmorType.HELMET, helmet);
        map.put(ArmorType.BODY, body);
        return map;
    }
}
