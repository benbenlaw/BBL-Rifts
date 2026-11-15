package com.benbenlaw.rifts.item;

import com.benbenlaw.rifts.Rifts;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import java.util.UUID;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RiftsDataComponents {

    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Rifts.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> RIFT_ENERGY =
            COMPONENTS.register("rift_energy", () ->
                    DataComponentType.<Integer>builder()
                            .persistent(ExtraCodecs.NON_NEGATIVE_INT)
                            .networkSynchronized(ByteBufCodecs.VAR_INT)
                            .cacheEncoding()
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceKey<Level>>> DISPLACER_DIMENSION =
            COMPONENTS.register("displacer_dimension", () ->
                    DataComponentType.<ResourceKey<Level>>builder()
                            .persistent(ResourceKey.codec(Registries.DIMENSION))
                            .networkSynchronized(ResourceKey.streamCodec(Registries.DIMENSION))
                            .cacheEncoding()
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> PORTAL_GUN_ID =
            COMPONENTS.register("portal_gun_id", () ->
                    DataComponentType.<UUID>builder()
                            .persistent(UUIDUtil.CODEC)
                            .networkSynchronized(UUIDUtil.STREAM_CODEC)
                            .cacheEncoding()
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DyeColor>> PORTAL_GUN_FIRST_COLOR =
            COMPONENTS.register("portal_gun_first_color", () ->
                    DataComponentType.<DyeColor>builder()
                            .persistent(DyeColor.CODEC)
                            .networkSynchronized(DyeColor.STREAM_CODEC)
                            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<DyeColor>> PORTAL_GUN_SECOND_COLOR =
            COMPONENTS.register("portal_gun_second_color", () ->
                    DataComponentType.<DyeColor>builder()
                            .persistent(DyeColor.CODEC)
                            .networkSynchronized(DyeColor.STREAM_CODEC)
                            .build());
}
