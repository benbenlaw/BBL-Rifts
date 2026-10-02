package com.benbenlaw.rifts.screen.furnace;

import com.benbenlaw.core.screen.SimpleAbstractContainerMenu;
import com.benbenlaw.core.screen.util.slot.InputSlot;
import com.benbenlaw.core.screen.util.slot.ResultSlot;
import com.benbenlaw.rifts.block.entity.RiftFurnaceBlockEntity;
import com.benbenlaw.rifts.screen.EpochopolisMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class RiftFurnaceMenu extends SimpleAbstractContainerMenu {

    public static final int LINES = RiftFurnaceBlockEntity.LINES;
    public static final int[] INPUT_X = {34, 104};
    public static final int[] OUTPUT_X = {80, 150};
    public static final int[] ROW_Y = {16, 34, 52};
    public static final int ARROW_OFFSET = 19;

    protected ContainerData data;

    public RiftFurnaceMenu(int containerID, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerID, inventory, extraData.readBlockPos(), new SimpleContainerData(LINES * 2 + 4));
    }

    public RiftFurnaceMenu(int containerID, Inventory inventory, BlockPos pos, ContainerData data) {
        super(EpochopolisMenuTypes.RIFT_FURNACE_MENU.get(), containerID, inventory, pos, LINES * 2);
        this.data = data;

        RiftFurnaceBlockEntity blockEntity = (RiftFurnaceBlockEntity) inventory.player.level().getBlockEntity(pos);
        assert blockEntity != null;

        for (int line = 0; line < LINES; line++) {
            this.addSlot(new InputSlot(blockEntity.getItemHandler(), blockEntity.getItemHandler()::set, line, inputX(line), rowY(line)));
        }
        for (int line = 0; line < LINES; line++) {
            this.addSlot(new ResultSlot(blockEntity.getItemHandler(), blockEntity.getItemHandler()::set, LINES + line, outputX(line), rowY(line)));
        }

        this.addDataSlots(data);
    }

    public static int inputX(int line) {
        return INPUT_X[line / ROW_Y.length];
    }

    public static int outputX(int line) {
        return OUTPUT_X[line / ROW_Y.length];
    }

    public static int rowY(int line) {
        return ROW_Y[line % ROW_Y.length];
    }

    public boolean isCooking(int line) {
        return data.get(line) > 0;
    }

    public int getScaledProgress(int line) {
        int progress = data.get(line);
        int maxProgress = data.get(LINES + line);
        int arrowSize = 24;

        return maxProgress != 0 && progress != 0 ? progress * arrowSize / maxProgress : 0;
    }

    public int getEnergy() {
        return (data.get(LINES * 2) & 0xFFFF) | (data.get(LINES * 2 + 1) << 16);
    }

    public int getCapacity() {
        return (data.get(LINES * 2 + 2) & 0xFFFF) | (data.get(LINES * 2 + 3) << 16);
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
