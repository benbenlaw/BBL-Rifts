package com.benbenlaw.rifts.block.capability;

import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class CreativeRiftEnergyHandler implements RiftEnergyHandler {

    public static final CreativeRiftEnergyHandler INSTANCE = new CreativeRiftEnergyHandler();

    private CreativeRiftEnergyHandler() {
    }

    @Override
    public long getAmountAsLong() {
        return Integer.MAX_VALUE;
    }

    @Override
    public long getCapacityAsLong() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        return amount;
    }

    @Override
    public boolean canInsert() {
        return false;
    }
}
