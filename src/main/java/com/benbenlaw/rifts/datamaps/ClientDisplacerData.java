package com.benbenlaw.rifts.datamaps;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Map;

public class ClientDisplacerData {

    private static Map<ResourceKey<Level>, DisplacerConversions> data = Map.of();

    public static void set(Map<ResourceKey<Level>, DisplacerConversions> newData) {
        data = Map.copyOf(newData);
    }

    public static Map<ResourceKey<Level>, DisplacerConversions> get() {
        return data;
    }
}
