package com.benbenlaw.rifts.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

public record DepletedResource(BlockPos pos, Identifier resourceType, long timestamp, Identifier depletedInEra) {

    public static final Codec<DepletedResource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        BlockPos.CODEC.fieldOf("pos").forGetter(DepletedResource::pos),
            Identifier.CODEC.fieldOf("resource_type").forGetter(DepletedResource::resourceType),
        Codec.LONG.fieldOf("timestamp").forGetter(DepletedResource::timestamp),
        Identifier.CODEC.fieldOf("depleted_in_era").forGetter(DepletedResource::depletedInEra)
    ).apply(instance, DepletedResource::new));

}