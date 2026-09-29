package com.benbenlaw.rifts.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class RiftsStartupConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;


    public static final ModConfigSpec.ConfigValue<Integer> SIMPLE_PYLON_GENERATION_RATE;
    public static final ModConfigSpec.ConfigValue<Integer> ADVANCED_PYLON_GENERATION_RATE;
    public static final ModConfigSpec.ConfigValue<Integer> ELITE_PYLON_GENERATION_RATE;
    public static final ModConfigSpec.ConfigValue<Integer> ULTIMATE_PYLON_GENERATION_RATE;


    static {

        BUILDER.comment("Rifts Startup").push("Pylons");

        SIMPLE_PYLON_GENERATION_RATE = BUILDER.defineInRange("simple_pylon_generation_rate", 1, 0, Integer.MAX_VALUE);
        ADVANCED_PYLON_GENERATION_RATE = BUILDER.defineInRange("advanced_pylon_generation_rate", 3, 0, Integer.MAX_VALUE);
        ELITE_PYLON_GENERATION_RATE = BUILDER.defineInRange("elite_pylon_generation_rate", 9, 0, Integer.MAX_VALUE);
        ULTIMATE_PYLON_GENERATION_RATE = BUILDER.defineInRange("ultimate_pylon_generation_rate", 12, 0, Integer.MAX_VALUE);

        BUILDER.pop();

        SPEC = BUILDER.build();

    }
}
