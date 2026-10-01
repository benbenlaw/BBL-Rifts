package com.benbenlaw.rifts.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class RiftsStartupConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;




    public static final ModConfigSpec.ConfigValue<Integer> PYLON_BASE_CAPACITY;
    public static final ModConfigSpec.ConfigValue<Integer> PYLON_CAPACITY_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Integer> PYLON_BASE_DRAW_PER_SECOND;
    public static final ModConfigSpec.ConfigValue<Integer> PYLON_DRAW_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Integer> STORAGE_CAPACITY;
    public static final ModConfigSpec.ConfigValue<Integer> PIPE_TRANSFER_PER_TICK;
    public static final ModConfigSpec.ConfigValue<Integer> CHUNK_BASE_RIFT_ENERGY;
    public static final ModConfigSpec.ConfigValue<Double> CHUNK_NOISE_VARIATION;
    public static final ModConfigSpec.ConfigValue<Integer> CHUNK_REGEN_TICKS_TO_FULL;
    public static final ModConfigSpec.ConfigValue<Double> CHUNK_NEIGHBOUR_BLEND;
    public static final ModConfigSpec.ConfigValue<Double> CHUNK_DIFFUSION_RATE;
    public static final ModConfigSpec.ConfigValue<Boolean> ELEMENTAL_ON_DEPLETION;
    public static final ModConfigSpec.ConfigValue<Double> ELEMENTAL_DEPLETION_THRESHOLD;

    static {

        BUILDER.comment("Rifts Startup").push("Pylons");


        PYLON_BASE_CAPACITY = BUILDER.comment("Rift energy a simple pylon can store")
                .defineInRange("pylon_base_capacity", 50000, 1, Integer.MAX_VALUE);
        PYLON_CAPACITY_MULTIPLIER = BUILDER.comment("Each pylon tier stores this many times more than the one below")
                .defineInRange("pylon_capacity_multiplier", 4, 1, 1000);
        PYLON_BASE_DRAW_PER_SECOND = BUILDER.comment("Rift energy per second a simple pylon draws from its chunk")
                .defineInRange("pylon_base_draw_per_second", 100, 1, Integer.MAX_VALUE);
        PYLON_DRAW_MULTIPLIER = BUILDER.comment("Each pylon tier draws this many times more per second than the one below")
                .defineInRange("pylon_draw_multiplier", 4, 1, 1000);

        BUILDER.pop();

        BUILDER.push("Transport");

        STORAGE_CAPACITY = BUILDER.comment("Rift energy a rift storage block can hold")
                .defineInRange("storage_capacity", 10000000, 1, Integer.MAX_VALUE);
        PIPE_TRANSFER_PER_TICK = BUILDER.comment("Most rift energy a pipe network moves per tick")
                .defineInRange("pipe_transfer_per_tick", 20000, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("Ambient Rift Energy");

        CHUNK_BASE_RIFT_ENERGY = BUILDER.comment("Rift energy a chunk holds before noise and biome modifiers")
                .defineInRange("chunk_base_rift_energy", 200000, 0, Integer.MAX_VALUE);
        CHUNK_NOISE_VARIATION = BUILDER.comment("How far noise can push chunk capacity from the base (0.75 = 25% to 175%)")
                .defineInRange("chunk_noise_variation", 0.75, 0.0, 1.0);
        CHUNK_REGEN_TICKS_TO_FULL = BUILDER.comment("Ticks for a fully drained chunk to refill")
                .defineInRange("chunk_regen_ticks_to_full", 36000, 1, Integer.MAX_VALUE);
        CHUNK_NEIGHBOUR_BLEND = BUILDER.comment("How much of a chunk's capacity comes from the average of its 8 neighbours (0 = none, 1 = all)")
                .defineInRange("chunk_neighbour_blend", 0.5, 0.0, 1.0);
        CHUNK_DIFFUSION_RATE = BUILDER.comment("Fraction of the fullness difference between neighbouring chunks that flows every 5 seconds (0 = no diffusion)")
                .defineInRange("chunk_diffusion_rate", 0.005, 0.0, 0.5);
        ELEMENTAL_ON_DEPLETION = BUILDER.comment("Summon a Rift Elemental when a pylon drains a chunk to 0%")
                .define("elemental_on_depletion", true);
        ELEMENTAL_DEPLETION_THRESHOLD = BUILDER.comment("A Rift Elemental is summoned when a draw leaves a chunk below this fraction of its capacity (0.05 = 5%). It can summon again after the chunk refills to twice this.")
                .defineInRange("elemental_depletion_threshold", 0.05, 0.0, 0.5);

        BUILDER.pop();

        SPEC = BUILDER.build();

    }
}
