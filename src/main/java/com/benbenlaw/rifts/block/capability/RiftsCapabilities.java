package com.benbenlaw.rifts.block.capability;

import com.benbenlaw.rifts.Rifts;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;

public class RiftsCapabilities {

    public static final BlockCapability<RiftEnergyHandler, Direction> RIFT_ENERGY =
            BlockCapability.createSided(Rifts.identifier("rift_energy"), RiftEnergyHandler.class);
}
