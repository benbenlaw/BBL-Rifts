package com.benbenlaw.rifts.screen.furnace;

import com.benbenlaw.core.screen.SimpleAbstractContainerMenu;
import com.benbenlaw.core.screen.util.slot.InputSlot;
import com.benbenlaw.core.screen.util.slot.ResultSlot;
import com.benbenlaw.rifts.block.entity.RiftFurnaceBlockEntity;
import com.benbenlaw.rifts.screen.RiftsMenuTypes;
import com.benbenlaw.rifts.screen.MachineLayouts;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;

public class RiftFurnaceMenu extends SimpleAbstractContainerMenu {

    private final int lines;
    protected ContainerData data;

    public RiftFurnaceMenu(int tier, int containerID, Inventory inventory, FriendlyByteBuf extraData) {
        this(tier, containerID, inventory, extraData.readBlockPos(), new SimpleContainerData(MachineLayouts.TIER_LINES[tier] * 2 + 4));
    }

    public RiftFurnaceMenu(int tier, int containerID, Inventory inventory, BlockPos pos, ContainerData data) {
        super(RiftsMenuTypes.RIFT_FURNACE_MENUS.get(tier).get(), containerID, inventory, pos, MachineLayouts.TIER_LINES[tier] * 2);
        this.lines = MachineLayouts.TIER_LINES[tier];
        this.data = data;

        RiftFurnaceBlockEntity blockEntity = (RiftFurnaceBlockEntity) inventory.player.level().getBlockEntity(pos);
        assert blockEntity != null;

        for (int line = 0; line < lines; line++) {
            this.addSlot(new InputSlot(blockEntity.getItemHandler(), blockEntity.getItemHandler()::set, line,
                    MachineLayouts.furnaceInputX(line, lines), MachineLayouts.furnaceRowY(line, lines)));
        }
        for (int line = 0; line < lines; line++) {
            this.addSlot(new ResultSlot(blockEntity.getItemHandler(), blockEntity.getItemHandler()::set, lines + line,
                    MachineLayouts.furnaceOutputX(line, lines), MachineLayouts.furnaceRowY(line, lines)));
        }

        this.addDataSlots(data);
    }

    private static int linesOf(MenuType<?> type) {
        for (int tier = 0; tier < MachineLayouts.TIER_LINES.length; tier++) {
            if (RiftsMenuTypes.RIFT_FURNACE_MENUS.get(tier).get() == type) return MachineLayouts.TIER_LINES[tier];
        }
        return MachineLayouts.TIER_LINES[0];
    }

    @Override
    protected void addPlayerInventory(Inventory playerInventory) {
        int extra = MachineLayouts.furnaceExtraHeight(linesOf(getType()));
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + extra + i * 18));
            }
        }
    }

    @Override
    protected void addPlayerHotbar(Inventory playerInventory) {
        int extra = MachineLayouts.furnaceExtraHeight(linesOf(getType()));
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142 + extra));
        }
    }

    public int getLines() {
        return lines;
    }

    public int getExtraHeight() {
        return MachineLayouts.furnaceExtraHeight(lines);
    }

    public boolean isCooking(int line) {
        return data.get(line) > 0;
    }

    public int getScaledProgress(int line) {
        int progress = data.get(line);
        int maxProgress = data.get(lines + line);
        int arrowSize = 24;

        return maxProgress != 0 && progress != 0 ? progress * arrowSize / maxProgress : 0;
    }

    public int getEnergy() {
        return (data.get(lines * 2) & 0xFFFF) | (data.get(lines * 2 + 1) << 16);
    }

    public int getCapacity() {
        return (data.get(lines * 2 + 2) & 0xFFFF) | (data.get(lines * 2 + 3) << 16);
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
