package com.benbenlaw.rifts.screen.surge;

import com.benbenlaw.core.screen.SimpleAbstractContainerMenu;
import com.benbenlaw.rifts.screen.RiftsMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class RiftSurgeCoreMenu extends SimpleAbstractContainerMenu {

    public static final int STAGES = 4;
    public static final int BAR_HEIGHT = 52;
    public static final int BAR_X = 38;
    public static final int BAR_SPACING = 28;
    public static final int BAR_Y = 16;

    protected ContainerData data;

    public RiftSurgeCoreMenu(int containerID, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerID, inventory, extraData.readBlockPos(), new SimpleContainerData(4));
    }

    public RiftSurgeCoreMenu(int containerID, Inventory inventory, BlockPos pos, ContainerData data) {
        super(RiftsMenuTypes.RIFT_SURGE_CORE_MENU.get(), containerID, inventory, pos, 0);
        this.data = data;
        this.addDataSlots(data);
    }

    public int getEnergy() {
        return (data.get(0) & 0xFFFF) | (data.get(1) << 16);
    }

    public int getCapacity() {
        return (data.get(2) & 0xFFFF) | (data.get(3) << 16);
    }

    public int getStageFilled(int stage) {
        long capacity = getCapacity();
        if (capacity <= 0) return 0;

        long stageSize = Math.max(1, capacity / STAGES);
        long inStage = Math.max(0, Math.min(stageSize, getEnergy() - stageSize * stage));
        return (int) (inStage * BAR_HEIGHT / stageSize);
    }
}
