package com.benbenlaw.rifts.block.capability;

import com.benbenlaw.core.block.entity.SyncableBlockEntity;

public class InfuserEnergyHandler extends SimpleRiftEnergyHandler {

    private final SyncableBlockEntity blockEntity;

    public InfuserEnergyHandler(int capacity, int maxTransfer, SyncableBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        super(capacity, maxTransfer);
    }

    @Override
    protected void onEnergyChanged(int previousAmount) {
        blockEntity.sync();
        super.onEnergyChanged(previousAmount);
    }
}
