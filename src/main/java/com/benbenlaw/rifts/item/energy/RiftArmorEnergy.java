package com.benbenlaw.rifts.item.energy;

import com.benbenlaw.rifts.config.RiftsStartupConfig;

public interface RiftArmorEnergy extends RiftEnergyItem {

    @Override
    default int getMaxEnergy() {
        return RiftsStartupConfig.RIFT_ARMOR_CAPACITY.get();
    }

    @Override
    default int getEnergyPerDamage() {
        return RiftsStartupConfig.RIFT_ARMOR_ENERGY_PER_DAMAGE.get();
    }
}
