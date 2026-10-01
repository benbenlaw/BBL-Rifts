package com.benbenlaw.rifts.item;

import com.benbenlaw.rifts.Rifts;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RiftsDataComponents {

    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Rifts.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> RIFT_ENERGY =
            COMPONENTS.register("rift_energy", () ->
                    DataComponentType.<Integer>builder()
                            .persistent(ExtraCodecs.NON_NEGATIVE_INT)
                            .networkSynchronized(ByteBufCodecs.VAR_INT)
                            .cacheEncoding()
                            .build());
}
