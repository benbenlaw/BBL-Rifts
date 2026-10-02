package com.benbenlaw.rifts.screen.crusher;

import com.benbenlaw.core.screen.SimpleAbstractContainerMenu;
import com.benbenlaw.core.screen.util.slot.InputSlot;
import com.benbenlaw.core.screen.util.slot.ResultSlot;
import com.benbenlaw.rifts.block.entity.RiftCrusherBlockEntity;
import com.benbenlaw.rifts.screen.EpochopolisMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class RiftCrusherMenu extends SimpleAbstractContainerMenu {

    public static final int INPUT_X = 56;
    public static final int INPUT_Y = 34;
    public static final int[] OUTPUT_X = {112, 130};
    public static final int[] OUTPUT_Y = {25, 43};
    public static final int ARROW_X = INPUT_X + 24;

    protected ContainerData data;

    public RiftCrusherMenu(int containerID, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerID, inventory, extraData.readBlockPos(), new SimpleContainerData(6));
    }

    public RiftCrusherMenu(int containerID, Inventory inventory, BlockPos pos, ContainerData data) {
        super(EpochopolisMenuTypes.RIFT_CRUSHER_MENU.get(), containerID, inventory, pos, 1 + RiftCrusherBlockEntity.OUTPUT_SLOTS);
        this.data = data;

        RiftCrusherBlockEntity blockEntity = (RiftCrusherBlockEntity) inventory.player.level().getBlockEntity(pos);
        assert blockEntity != null;

        this.addSlot(new InputSlot(blockEntity.getItemHandler(), blockEntity.getItemHandler()::set, 0, INPUT_X, INPUT_Y));
        for (int i = 0; i < RiftCrusherBlockEntity.OUTPUT_SLOTS; i++) {
            this.addSlot(new ResultSlot(blockEntity.getItemHandler(), blockEntity.getItemHandler()::set, 1 + i,
                    OUTPUT_X[i % OUTPUT_X.length], OUTPUT_Y[i / OUTPUT_X.length]));
        }

        this.addDataSlots(data);
    }

    public boolean isCrushing() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = data.get(0);
        int maxProgress = data.get(1);
        int arrowSize = 24;

        return maxProgress != 0 && progress != 0 ? progress * arrowSize / maxProgress : 0;
    }

    public int getEnergy() {
        return (data.get(2) & 0xFFFF) | (data.get(3) << 16);
    }

    public int getCapacity() {
        return (data.get(4) & 0xFFFF) | (data.get(5) << 16);
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
