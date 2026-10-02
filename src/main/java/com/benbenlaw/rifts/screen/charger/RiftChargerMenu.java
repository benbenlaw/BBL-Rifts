package com.benbenlaw.rifts.screen.charger;

import com.benbenlaw.core.screen.SimpleAbstractContainerMenu;
import com.benbenlaw.core.screen.util.slot.InputSlot;
import com.benbenlaw.rifts.block.entity.RiftChargerBlockEntity;
import com.benbenlaw.rifts.screen.EpochopolisMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class RiftChargerMenu extends SimpleAbstractContainerMenu {

    public static final int SLOT_X = 62;
    public static final int SLOT_PITCH = 18;
    public static final int SLOT_Y = 34;

    protected ContainerData data;

    public RiftChargerMenu(int containerID, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerID, inventory, extraData.readBlockPos(), new SimpleContainerData(4));
    }

    public RiftChargerMenu(int containerID, Inventory inventory, BlockPos pos, ContainerData data) {
        super(EpochopolisMenuTypes.RIFT_CHARGER_MENU.get(), containerID, inventory, pos, RiftChargerBlockEntity.SLOTS);
        this.data = data;

        RiftChargerBlockEntity blockEntity = (RiftChargerBlockEntity) inventory.player.level().getBlockEntity(pos);
        assert blockEntity != null;

        for (int i = 0; i < RiftChargerBlockEntity.SLOTS; i++) {
            this.addSlot(new InputSlot(blockEntity.getItemHandler(), blockEntity.getItemHandler()::set, i, SLOT_X + i * SLOT_PITCH, SLOT_Y));
        }

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
