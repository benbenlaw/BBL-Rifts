package com.benbenlaw.rifts.block.capability;

import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class RiftEnergyViews {

    public static RiftEnergyHandler insertOnly(RiftEnergyHandler handler) {
        return new View(handler, true, false);
    }

    public static RiftEnergyHandler extractOnly(RiftEnergyHandler handler) {
        return new View(handler, false, true);
    }

    private record View(RiftEnergyHandler handler, boolean insertable, boolean extractable) implements RiftEnergyHandler {

        @Override
        public long getAmountAsLong() {
            return handler.getAmountAsLong();
        }

        @Override
        public long getCapacityAsLong() {
            return handler.getCapacityAsLong();
        }

        @Override
        public int insert(int amount, TransactionContext transaction) {
            return insertable ? handler.insert(amount, transaction) : 0;
        }

        @Override
        public int extract(int amount, TransactionContext transaction) {
            return extractable ? handler.extract(amount, transaction) : 0;
        }

        @Override
        public boolean canInsert() {
            return insertable;
        }

        @Override
        public boolean canExtract() {
            return extractable;
        }
    }
}
