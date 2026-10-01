package com.benbenlaw.rifts.block.capability;

import net.minecraft.world.inventory.ContainerData;

public class RiftEnergyContainerData implements ContainerData {

    private final RiftEnergyHandler handler;

    public RiftEnergyContainerData(RiftEnergyHandler handler) {
        this.handler = handler;
    }

    @Override
    public int get(int index) {
        return switch (index) {
            case 0 -> handler.getAmountAsInt() & 0xFFFF;
            case 1 -> handler.getAmountAsInt() >>> 16;
            case 2 -> handler.getCapacityAsInt() & 0xFFFF;
            case 3 -> handler.getCapacityAsInt() >>> 16;
            default -> 0;
        };
    }

    @Override
    public void set(int index, int value) {
    }

    @Override
    public int getCount() {
        return 4;
    }
}
