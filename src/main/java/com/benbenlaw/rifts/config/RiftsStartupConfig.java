package com.benbenlaw.rifts.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class RiftsStartupConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;




    public static final ModConfigSpec.ConfigValue<Integer> PYLON_BASE_CAPACITY;
    public static final ModConfigSpec.ConfigValue<Integer> PYLON_CAPACITY_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Integer> PYLON_BASE_DRAW_PER_SECOND;
    public static final ModConfigSpec.ConfigValue<Integer> PYLON_DRAW_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Integer> CRUSHER_ENERGY_CAPACITY;
    public static final ModConfigSpec.ConfigValue<Integer> FURNACE_ENERGY_CAPACITY;
    public static final ModConfigSpec.ConfigValue<Integer> FURNACE_ENERGY_PER_TICK;
    public static final ModConfigSpec.ConfigValue<Integer> FURNACE_SPEED;
    public static final ModConfigSpec.ConfigValue<Integer> STORAGE_CAPACITY;
    public static final ModConfigSpec.ConfigValue<Integer> PIPE_TRANSFER_PER_TICK;
    public static final ModConfigSpec.ConfigValue<Integer> CHUNK_BASE_RIFT_ENERGY;
    public static final ModConfigSpec.ConfigValue<Double> CHUNK_NOISE_VARIATION;
    public static final ModConfigSpec.ConfigValue<Integer> CHUNK_REGEN_TICKS_TO_FULL;
    public static final ModConfigSpec.ConfigValue<Double> CHUNK_NEIGHBOUR_BLEND;
    public static final ModConfigSpec.ConfigValue<Double> CHUNK_DIFFUSION_RATE;
    public static final ModConfigSpec.ConfigValue<Boolean> ELEMENTAL_ON_DEPLETION;
    public static final ModConfigSpec.ConfigValue<Integer> ACCELERATOR_BASE_EXTRA_TICKS;
    public static final ModConfigSpec.ConfigValue<Integer> ACCELERATOR_EXTRA_TICKS_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Integer> ACCELERATOR_BASE_ENERGY_PER_TICK;
    public static final ModConfigSpec.ConfigValue<Integer> ACCELERATOR_ENERGY_MULTIPLIER;
    public static final ModConfigSpec.ConfigValue<Integer> ACCELERATOR_MAX_EXTRA_TICKS;
    public static final ModConfigSpec.ConfigValue<Double> ACCELERATOR_RANDOM_TICKS_PER_EXTRA_TICK;
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

        BUILDER.push("Blocks");

        CRUSHER_ENERGY_CAPACITY = BUILDER.comment("Rift energy a rift crusher can hold")
                .defineInRange("crusher_energy_capacity", 50000, 1, Integer.MAX_VALUE);
        FURNACE_ENERGY_CAPACITY = BUILDER.comment("Rift energy a rift furnace can hold")
                .defineInRange("furnace_energy_capacity", 50000, 1, Integer.MAX_VALUE);
        FURNACE_ENERGY_PER_TICK = BUILDER.comment("Rift energy a rift furnace uses every tick for each of its 6 lines that is smelting")
                .defineInRange("furnace_energy_per_tick", 40, 1, Integer.MAX_VALUE);
        FURNACE_SPEED = BUILDER.comment("How many times faster than a normal furnace a rift furnace smelts")
                .defineInRange("furnace_speed", 2, 1, 100);
        STORAGE_CAPACITY = BUILDER.comment("Rift energy a rift storage block can hold")
                .defineInRange("storage_capacity", 10000000, 1, Integer.MAX_VALUE);
        PIPE_TRANSFER_PER_TICK = BUILDER.comment("Most rift energy a pipe network moves per tick")
                .defineInRange("pipe_transfer_per_tick", 20000, 1, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("Tick Accelerator");

        ACCELERATOR_BASE_EXTRA_TICKS = BUILDER.comment("Extra ticks per tick a basic tick accelerator gives each adjacent block")
                .defineInRange("accelerator_base_extra_ticks", 2, 1, 1000);
        ACCELERATOR_EXTRA_TICKS_MULTIPLIER = BUILDER.comment("Each accelerator tier gives this many times more extra ticks than the one below")
                .defineInRange("accelerator_extra_ticks_multiplier", 2, 1, 100);
        ACCELERATOR_BASE_ENERGY_PER_TICK = BUILDER.comment("Rift energy per tick a basic tick accelerator uses while it is accelerating something")
                .defineInRange("accelerator_base_energy_per_tick", 50, 1, Integer.MAX_VALUE);
        ACCELERATOR_ENERGY_MULTIPLIER = BUILDER.comment("Each accelerator tier uses this many times more energy per tick than the one below")
                .defineInRange("accelerator_energy_multiplier", 3, 1, 100);
        ACCELERATOR_MAX_EXTRA_TICKS = BUILDER.comment("Most extra ticks a single block can receive per tick when several accelerators touch it")
                .defineInRange("accelerator_max_extra_ticks", 32, 1, 1000);
        ACCELERATOR_RANDOM_TICKS_PER_EXTRA_TICK = BUILDER.comment("Random ticks (crops, saplings and similar) a block gets per tick for each extra tick an accelerator gives it. 0.0625 makes the ultimate tier (16 extra ticks) give one random tick every tick")
                .defineInRange("accelerator_random_ticks_per_extra_tick", 0.0625, 0.0, 1000.0);

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
