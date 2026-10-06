package com.benbenlaw.rifts.block.entity.renderer;

import com.benbenlaw.rifts.block.entity.RiftGeneratorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

public class RiftGeneratorBlockEntityRenderer implements BlockEntityRenderer<RiftGeneratorBlockEntity, RiftGeneratorBlockEntityRendererState> {

    public RiftGeneratorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public RiftGeneratorBlockEntityRendererState createRenderState() {
        return new RiftGeneratorBlockEntityRendererState();
    }

    @Override
    public void extractRenderState(RiftGeneratorBlockEntity blockEntity, RiftGeneratorBlockEntityRendererState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        RiftGeneratorBlockEntity.RiftState rs = blockEntity.getRiftState();
        state.riftOpen = rs == RiftGeneratorBlockEntity.RiftState.ACTIVE || rs == RiftGeneratorBlockEntity.RiftState.CLOSING;
        state.riftClosing = rs == RiftGeneratorBlockEntity.RiftState.CLOSING;
        state.blockPos = blockEntity.getBlockPos();

        state.activeBase = blockEntity.getActiveBaseColor();
        state.closingBase = blockEntity.getClosingBaseColor();
    }

    @Override
    public void submit(RiftGeneratorBlockEntityRendererState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
        if (!state.riftOpen) return;

        long millis = System.currentTimeMillis();
        float ageTicks = millis / 50.0F;
        float alpha = state.riftClosing ? 0.7F + 0.3F * Mth.sin(millis / 90.0F) : 1.0F;

        BlockPos pos = state.blockPos;
        RiftPortalRendering.submit(poseStack, collector, cameraRenderState.pos, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                ageTicks, RiftPortalRendering.PORTAL_TINTABLE, state.riftClosing ? state.closingBase : state.activeBase, alpha);
    }

    @Override public boolean shouldRender(RiftGeneratorBlockEntity be, Vec3 cameraPos) {
        return true;
    }

    @Override public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override public int getViewDistance() {
        return 32;
    }

    @Override
    public @NonNull AABB getRenderBoundingBox(RiftGeneratorBlockEntity blockEntity) {
        return AABB.encapsulatingFullBlocks(
                blockEntity.getBlockPos().above(4).north(2).east(2),
                blockEntity.getBlockPos().below(1).south(2).west(2));
    }
}
