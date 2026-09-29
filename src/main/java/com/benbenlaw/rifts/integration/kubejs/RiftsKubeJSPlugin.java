package com.benbenlaw.rifts.integration.kubejs;

import com.benbenlaw.rifts.Rifts;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.BuilderTypeRegistry;
import net.minecraft.core.registries.Registries;

public class RiftsKubeJSPlugin implements KubeJSPlugin {

    public static EventGroup GROUP = EventGroup.of("EpochopolisCompatEvents");

    @Override
    public void registerBuilderTypes(BuilderTypeRegistry registry) {
        registry.of(Registries.ITEM, r -> r.add(Rifts.identifier("displacer"), DisplacerBuilder.class, DisplacerBuilder::new));
    }
}