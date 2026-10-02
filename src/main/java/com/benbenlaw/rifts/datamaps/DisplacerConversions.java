package com.benbenlaw.rifts.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

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
}