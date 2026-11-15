package com.benbenlaw.rifts.datamaps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record RiftEnergyModifier(float multiplier) {
    public static final Codec<RiftEnergyModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.floatRange(0F, 100F).fieldOf("multiplier").forGetter(RiftEnergyModifier::multiplier)
    ).apply(instance, RiftEnergyModifier::new));
}
