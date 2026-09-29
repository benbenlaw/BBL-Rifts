package com.benbenlaw.rifts.block.entity.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.BlockPos;

public class RiftGeneratorBlockEntityRendererState extends BlockEntityRenderState {
    public boolean riftOpen;
    public boolean riftClosing;
    public BlockPos blockPos;

    public float[] activeBase;
    public float[] activeBright;
    public float[] activeBoltCore;
    public float[] closingBase;
    public float[] closingBright;
    public float[] closingBoltCore;
}