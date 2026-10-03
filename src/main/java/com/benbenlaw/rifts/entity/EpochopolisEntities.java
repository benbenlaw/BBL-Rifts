package com.benbenlaw.rifts.entity;

import com.benbenlaw.rifts.Rifts;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EpochopolisEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Rifts.MOD_ID);

    public static final ResourceKey<EntityType<?>> DISPLACER_KEY =
            ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Rifts.MOD_ID, "displacer"));

    public static final ResourceKey<EntityType<?>> RIFT_ELEMENTAL_KEY =
            ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Rifts.MOD_ID, "rift_elemental"));

    public static final ResourceKey<EntityType<?>> NATURAL_RIFT_KEY =
            ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Rifts.MOD_ID, "natural_rift"));

    public static final Supplier<EntityType<NaturalRift>> NATURAL_RIFT =
            ENTITY_TYPES.register("natural_rift", () -> EntityType.Builder.<NaturalRift>of(NaturalRift::new, MobCategory.MISC)
                    .sized(1.2F, 2.5F)
                    .fireImmune()
                    .noSummon()
                    .clientTrackingRange(12)
                    .build(NATURAL_RIFT_KEY));

    public static final Supplier<EntityType<RiftElemental>> RIFT_ELEMENTAL =
            ENTITY_TYPES.register("rift_elemental", () -> EntityType.Builder.of(RiftElemental::new, MobCategory.MONSTER)
                    .sized(1.4F, 2.7F)
                    .clientTrackingRange(10)
                    .build(RIFT_ELEMENTAL_KEY));

    public static final Supplier<EntityType<DisplacerEntity>> DISPLACER =
            ENTITY_TYPES.register("displacer", () -> EntityType.Builder.<DisplacerEntity>of(DisplacerEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .build(DISPLACER_KEY));

}
