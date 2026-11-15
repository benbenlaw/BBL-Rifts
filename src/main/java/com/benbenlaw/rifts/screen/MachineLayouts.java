package com.benbenlaw.rifts.screen;

public final class MachineLayouts {

    public static final int[] TIER_LINES = {1, 3, 6, 10};
    public static final String[] TIER_NAMES = {"basic", "advanced", "elite", "ultimate"};

    private static final int TOP = 16;
    private static final int ROW = 18;
    private static final int PLAYER_INVENTORY_Y = 84;

    private MachineLayouts() {}

    public static int tierOf(int lines) {
        for (int tier = 0; tier < TIER_LINES.length; tier++) {
            if (TIER_LINES[tier] == lines) return tier;
        }
        return 0;
    }

    public static int extraHeight(int rows) {
        return Math.max(0, TOP + rows * ROW + 13 - PLAYER_INVENTORY_Y);
    }

    private static int pad(int rows) {
        return (Math.max(3, rows) - rows) * 9;
    }

    public static int furnaceColumns(int lines) {
        return lines <= 3 ? 1 : 2;
    }

    public static int furnaceRows(int lines) {
        int columns = furnaceColumns(lines);
        return (lines + columns - 1) / columns;
    }

    public static int furnaceInputX(int line, int lines) {
        if (furnaceColumns(lines) == 1) return 66;
        return line / furnaceRows(lines) == 0 ? 34 : 104;
    }

    public static int furnaceOutputX(int line, int lines) {
        return furnaceInputX(line, lines) + 46;
    }

    public static int furnaceArrowX(int line, int lines) {
        return furnaceInputX(line, lines) + 19;
    }

    public static int furnaceRowY(int line, int lines) {
        int rows = furnaceRows(lines);
        return TOP + pad(rows) + (line % rows) * ROW;
    }

    public static int furnaceExtraHeight(int lines) {
        return extraHeight(furnaceRows(lines));
    }

    public static final int CRUSHER_OUTPUT_COLUMNS = 2;
    private static final int[] CRUSHER_OUTPUT_X = {122, 140};

    public static int crusherInputColumns(int lines) {
        return lines <= 3 ? 1 : 2;
    }

    public static int crusherInputRows(int lines) {
        int columns = crusherInputColumns(lines);
        return (lines + columns - 1) / columns;
    }

    public static int crusherOutputRows(int lines) {
        return lines == 1 ? 2 : crusherInputRows(lines);
    }

    public static int crusherOutputs(int lines) {
        return crusherOutputRows(lines) * CRUSHER_OUTPUT_COLUMNS;
    }

    public static int crusherInputX(int line, int lines) {
        if (crusherInputColumns(lines) == 1) return lines == 1 ? 60 : 40;
        return line / crusherInputRows(lines) == 0 ? 28 : 74;
    }

    public static int crusherArrowX(int line, int lines) {
        return crusherInputX(line, lines) + 20;
    }

    public static int crusherInputY(int line, int lines) {
        int rows = crusherInputRows(lines);
        return TOP + padFor(lines, rows) + (line % rows) * ROW;
    }

    public static int crusherOutputX(int slot) {
        return CRUSHER_OUTPUT_X[slot % CRUSHER_OUTPUT_COLUMNS];
    }

    public static int crusherOutputY(int slot, int lines) {
        return TOP + padFor(lines, crusherOutputRows(lines)) + (slot / CRUSHER_OUTPUT_COLUMNS) * ROW;
    }

    public static int crusherExtraHeight(int lines) {
        return extraHeight(Math.max(crusherInputRows(lines), crusherOutputRows(lines)));
    }

    private static int padFor(int lines, int rows) {
        int area = Math.max(3, Math.max(crusherInputRows(lines), crusherOutputRows(lines)));
        return (area - rows) * 9;
    }
}
