package com.benbenlaw.rifts.particle;

import com.benbenlaw.rifts.Rifts;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RiftsParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Rifts.MOD_ID);

    public static final Supplier<SimpleParticleType> RIFT_ABSORB = PARTICLES.register("rift_absorb", () -> new SimpleParticleType(false));
}
