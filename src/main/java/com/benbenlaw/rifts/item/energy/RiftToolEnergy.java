package com.benbenlaw.rifts.item.energy;

import com.benbenlaw.rifts.config.RiftsStartupConfig;

public interface RiftToolEnergy extends RiftEnergyItem {

    @Override
    default int getMaxEnergy() {
        return RiftsStartupConfig.RIFT_TOOL_CAPACITY.get();
    }

    @Override
    default int getEnergyPerDamage() {
        return RiftsStartupConfig.RIFT_TOOL_ENERGY_PER_DAMAGE.get();
    }
}
