package com.benbenlaw.rifts.block.capability;

import com.google.common.primitives.Ints;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public interface RiftEnergyHandler {

    long getAmountAsLong();

    default int getAmountAsInt() {
        return Ints.saturatedCast(getAmountAsLong());
    }

    long getCapacityAsLong();

    default int getCapacityAsInt() {
        return Ints.saturatedCast(getCapacityAsLong());
    }

    int insert(int amount, TransactionContext transaction);

    int extract(int amount, TransactionContext transaction);

    default boolean canInsert() {
        return true;
    }

    default boolean canExtract() {
        return true;
    }
}
