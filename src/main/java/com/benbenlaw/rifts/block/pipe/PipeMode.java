package com.benbenlaw.rifts.block.pipe;

public enum PipeMode {
    INSERT("insert"),
    EXTRACT("extract"),
    OFF("off");

    private final String name;

    PipeMode(String name) {
        this.name = name;
    }

    public String getTranslationKey() {
        return "message.rifts.pipe_mode." + name;
    }

    public PipeMode next() {
        return values()[(ordinal() + 1) % values().length];
    }
}
