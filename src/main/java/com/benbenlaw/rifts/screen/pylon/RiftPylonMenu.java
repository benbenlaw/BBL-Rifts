package com.benbenlaw.rifts.screen.pylon;

import com.benbenlaw.core.screen.SimpleAbstractContainerMenu;
import com.benbenlaw.rifts.screen.EpochopolisMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class RiftPylonMenu extends SimpleAbstractContainerMenu {

    protected ContainerData data;
    protected BlockPos blockPos;

    public RiftPylonMenu(int containerID, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerID, inventory, extraData.readBlockPos(), new SimpleContainerData(4));
    }

    public RiftPylonMenu(int containerID, Inventory inventory, BlockPos pos, ContainerData data) {
        super(EpochopolisMenuTypes.RIFT_PYLON_MENU.get(), containerID, inventory, pos, 0);
        this.data = data;
        this.blockPos = pos;
        this.addDataSlots(data);
    }

    public int getEnergy() {
        return (data.get(0) & 0xFFFF) | (data.get(1) << 16);
    }

    public int getCapacity() {
        return (data.get(2) & 0xFFFF) | (data.get(3) << 16);
    }

    public boolean hasEnergy() {
        return getEnergy() > 0;
    }

    public int getEnergyFilled() {
        long energy = getEnergy();
        long capacity = getCapacity();
        int barHeight = 52;

        return capacity != 0 && energy != 0 ? (int) (energy * barHeight / capacity) : 0;
    }
}
