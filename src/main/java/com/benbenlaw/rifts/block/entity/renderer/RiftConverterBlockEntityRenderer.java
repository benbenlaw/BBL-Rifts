package com.benbenlaw.rifts.block.entity.renderer;

import com.benbenlaw.rifts.block.entity.RiftConverterBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RiftConverterBlockEntityRenderer implements BlockEntityRenderer<RiftConverterBlockEntity, RiftConverterBlockEntityRenderer.State> {

    private static final float INCOMING_SCALE = 0.996F;

    public static class State extends BlockEntityRenderState {
        public @Nullable BlockState source;
        public @Nullable BlockState result;
        public float progress;
        public int topLight;
    }

    public RiftConverterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(RiftConverterBlockEntity blockEntity, State state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        state.source = null;
        state.result = null;
        if (!blockEntity.isConverting() || blockEntity.getLevel() == null) return;

        state.progress = blockEntity.getProgressFraction();
        state.source = blockEntity.getSourceState();
        state.result = blockEntity.getResultState();
        state.topLight = LevelRenderer.getLightCoords(blockEntity.getLevel(), blockEntity.getBlockPos().above());
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.source != null) {
            submitFaded(state.source, 1.0F - state.progress, 1.0F, state.topLight, poseStack, collector);
        }
        if (state.result != null) {
            submitFaded(state.result, state.progress, INCOMING_SCALE, state.topLight, poseStack, collector);
        }
    }

    private static void submitFaded(BlockState blockState, float alpha, float scale, int light, PoseStack poseStack, SubmitNodeCollector collector) {
        if (alpha < 0.02F || blockState.getRenderShape() != RenderShape.MODEL) return;

        BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(blockState);
        List<BlockStateModelPart> parts = new ArrayList<>();
        model.collectParts(RandomSource.create(42L), parts);

        int color = ((int) (Math.min(1.0F, alpha) * 255) << 24) | 0xFFFFFF;
        QuadInstance instance = new QuadInstance();
        for (int vertex = 0; vertex < 4; vertex++) {
            instance.setColor(vertex, color);
            instance.setLightCoords(vertex, light);
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-0.5, -0.5, -0.5);
        collector.submitCustomGeometry(poseStack, Sheets.translucentBlockSheet(), (pose, consumer) -> {
            for (BlockStateModelPart part : parts) {
                for (Direction direction : sides()) {
                    for (BakedQuad quad : part.getQuads(direction)) {
                        consumer.putBakedQuad(pose, quad, instance);
                    }
                }
            }
        });
        poseStack.popPose();
    }

    private static List<Direction> sides() {
        List<Direction> sides = new ArrayList<>(List.of(Direction.values()));
        sides.add(null);
        return sides;
    }

    @Override
    public @NonNull AABB getRenderBoundingBox(RiftConverterBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
    }
}
