package com.benbenlaw.rifts.screen.generator;

import com.benbenlaw.core.screen.SimpleAbstractContainerMenu;
import com.benbenlaw.core.screen.util.slot.InputSlot;
import com.benbenlaw.rifts.block.entity.RiftGeneratorBlockEntity;
import com.benbenlaw.rifts.screen.EpochopolisMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;

public class RiftGeneratorMenu extends SimpleAbstractContainerMenu {

    protected RiftGeneratorBlockEntity blockEntity;
    protected Level level;
    protected ContainerData data;
    protected Player player;
    protected BlockPos blockPos;

    public RiftGeneratorMenu(int containerID, Inventory inventory, FriendlyByteBuf extraData) {
        this(containerID, inventory, extraData.readBlockPos(), new SimpleContainerData(6));
    }

    public RiftGeneratorMenu(int containerID, Inventory inventory, BlockPos pos, ContainerData data) {
        super(EpochopolisMenuTypes.RIFT_GENERATOR_MENU.get(), containerID, inventory, pos, 4);
        this.player = inventory.player;
        this.level = inventory.player.level();
        this.data = data;
        this.blockPos = pos;
        this.blockEntity = (RiftGeneratorBlockEntity) level.getBlockEntity(pos);

        assert blockEntity != null;

        this.addSlot(new InputSlot(blockEntity.getItemHandler(), blockEntity.getItemHandler()::set,
                0, 60, 23));

        //this.addSlot(new InputSlot(blockEntity.getItemHandler(), blockEntity.getItemHandler()::set,
        //        1, 8 * 18 + 20, 54));
//
        //this.addSlot(new InputSlot(blockEntity.getItemHandler(), blockEntity.getItemHandler()::set,
        //        2, 8 * 18 + 40, 54));
//
        //this.addSlot(new InputSlot(blockEntity.getItemHandler(), blockEntity.getItemHandler()::set,
        //        3, 8 * 18 + 60, 54));

        this.addDataSlots(data);
    }

    public boolean hasEnergy() {
        return data.get(4) > 0;
    }

    public int getEnergyFilled() {

        int progress = this.data.get(4);
        int maxProgress = this.data.get(5);  // Max Progress
        int progressArrowSize = 52; // This is the height in pixels of your arrow

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {

        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 24; // This is the height/width in pixels of your arrow

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }
}
