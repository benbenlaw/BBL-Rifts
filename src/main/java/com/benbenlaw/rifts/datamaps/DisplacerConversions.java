package com.benbenlaw.rifts.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

import java.util.Map;

public record DisplacerConversions(int radius, float chance, Map<Block, Block> conversions) {
    public static final Codec<DisplacerConversions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("radius").forGetter(DisplacerConversions::radius),
            Codec.floatRange(0F, 1F).fieldOf("chance").forGetter(DisplacerConversions::chance),
            Codec.unboundedMap(BuiltInRegistries.BLOCK.byNameCodec(), BuiltInRegistries.BLOCK.byNameCodec())
                    .fieldOf("conversions").forGetter(DisplacerConversions::conversions)
    ).apply(instance, DisplacerConversions::new));
}