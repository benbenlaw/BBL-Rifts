package com.benbenlaw.rifts.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.LevelStem;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public record DisplacerConversions(int radius, float chance, Map<Block, Block> conversions, Map<TagKey<Block>, Block> tagConversions, Map<EntityType<?>, EntityType<?>> entityConversions) {
    public static final Codec<DisplacerConversions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("radius").forGetter(DisplacerConversions::radius),
            Codec.floatRange(0F, 1F).fieldOf("chance").forGetter(DisplacerConversions::chance),
            Codec.unboundedMap(BuiltInRegistries.BLOCK.byNameCodec(), BuiltInRegistries.BLOCK.byNameCodec())
                    .optionalFieldOf("conversions", Map.of()).forGetter(DisplacerConversions::conversions),
            Codec.unboundedMap(TagKey.hashedCodec(Registries.BLOCK), BuiltInRegistries.BLOCK.byNameCodec())
                    .optionalFieldOf("tag_conversions", Map.of()).forGetter(DisplacerConversions::tagConversions),
            Codec.unboundedMap(BuiltInRegistries.ENTITY_TYPE.byNameCodec(), BuiltInRegistries.ENTITY_TYPE.byNameCodec())
                    .optionalFieldOf("entity_conversions", Map.of()).forGetter(DisplacerConversions::entityConversions)
    ).apply(instance, DisplacerConversions::new));

    public static @Nullable DisplacerConversions find(RegistryAccess access, ResourceKey<Level> dimension) {
        return access.lookupOrThrow(Registries.LEVEL_STEM)
                .get(ResourceKey.create(Registries.LEVEL_STEM, dimension.identifier()))
                .map(holder -> holder.getData(RiftsDataMaps.DISPLACER_HIT_RESULTS))
                .orElse(null);
    }

    public static Map<ResourceKey<Level>, DisplacerConversions> all(RegistryAccess access) {
        Map<ResourceKey<Level>, DisplacerConversions> result = new HashMap<>();
        access.lookupOrThrow(Registries.LEVEL_STEM).listElements().forEach(holder -> {
            DisplacerConversions conversions = holder.getData(RiftsDataMaps.DISPLACER_HIT_RESULTS);
            if (conversions != null) {
                result.put(ResourceKey.create(Registries.DIMENSION, holder.key().identifier()), conversions);
            }
        });
        return result;
    }
}
