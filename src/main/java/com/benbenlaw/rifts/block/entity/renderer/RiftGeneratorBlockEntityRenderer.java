package com.benbenlaw.rifts.block.entity.renderer;

import com.benbenlaw.rifts.block.entity.RiftGeneratorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;

import java.util.Random;
import java.util.function.BiConsumer;

public class RiftGeneratorBlockEntityRenderer implements BlockEntityRenderer<RiftGeneratorBlockEntity, RiftGeneratorBlockEntityRendererState> {

    private static final int FLICKER_INTERVAL_MS = 90;
    private static final double LOWER_HEIGHT = 1.2;
    private static final double UPPER_HEIGHT = 3.5;

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
        state.activeBright = blockEntity.getActiveBrightColor();
        state.activeBoltCore = blockEntity.getActiveBoltCore();
        state.closingBase = blockEntity.getClosingBaseColor();
        state.closingBright = blockEntity.getClosingBrightColor();
        state.closingBoltCore = blockEntity.getClosingBoltCore();
    }

    @Override
    public void submit(RiftGeneratorBlockEntityRendererState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        if (!state.riftOpen) return;

        long time = System.currentTimeMillis();
        float spin = (time % 4000L) / 4000f * 360f;

        float[] color = state.riftClosing ? state.closingBase : state.activeBase;
        float[] brightColor = state.riftClosing ? state.closingBright : state.activeBright;
        float[] boltCore = state.riftClosing ? state.closingBoltCore : state.activeBoltCore;

        BlockPos pos = state.blockPos;

        Vec3 lowerWorld = new Vec3(pos.getX() + 0.5, pos.getY() + LOWER_HEIGHT, pos.getZ() + 0.5);
        Vec3 upperWorld = new Vec3(pos.getX() + 0.5, pos.getY() + UPPER_HEIGHT, pos.getZ() + 0.5);

        submitRift(poseStack, submitNodeCollector, cameraRenderState.pos, lowerWorld, upperWorld, time, spin, color, brightColor, boltCore);
    }

    public void submitRift(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, Vec3 camera, Vec3 lowerWorld, Vec3 upperWorld,
                           long time, float spin, float[] color, float[] brightColor, float[] boltCore) {
        Vec3 lowerOrigin = lowerWorld.subtract(camera);
        Vec3 upperOrigin = upperWorld.subtract(camera);

        submitVortex(poseStack, submitNodeCollector, lowerOrigin, spin, color, brightColor);
        submitArcs(poseStack, submitNodeCollector, lowerOrigin, time, color, boltCore, false);

        submitVortex(poseStack, submitNodeCollector, upperOrigin, -spin * 0.7f, color, brightColor);
        submitArcs(poseStack, submitNodeCollector, upperOrigin, time + 977, color, boltCore, true);

        submitBeam(poseStack, submitNodeCollector, lowerOrigin, upperOrigin, color);
    }

    private void submitVortex(PoseStack poseStack, SubmitNodeCollector collector, Vec3 origin, float spin, float[] color, float[] brightColor) {
        submitAt(poseStack, collector, RIFT_GEOMETRY, origin, (pose, consumer) -> {
            float pulse = 0.9f + 0.1f * (float) Math.sin(System.currentTimeMillis() / 150.0);

            float[][] layers = {
                    {0.75f, 0.20f, 20, 1.0f, 0f, 8f, 0.05f},
                    {0.55f, 0.35f, 18, -1.4f, 0.1f, -14f, -0.03f},
                    {0.38f, 0.55f, 16, 1.8f, 0.25f, 20f, 0.08f},
                    {0.20f, 0.85f, 12, -2.5f, 0.5f, -26f, -0.05f},
            };

            for (float[] layer : layers) {
                float r = layer[0] * pulse;
                float alpha = layer[1];
                int segments = (int) layer[2];
                float layerSpin = spin * layer[3];
                float boost = layer[4];
                float tiltDeg = layer[5];
                float yOffset = layer[6];

                float lr = color[0] + (brightColor[0] - color[0]) * boost;
                float lg = color[1] + (brightColor[1] - color[1]) * boost;
                float lb = color[2] + (brightColor[2] - color[2]) * boost;

                float tiltRad = (float) Math.toRadians(tiltDeg);
                float cosT = (float) Math.cos(tiltRad);
                float sinT = (float) Math.sin(tiltRad);

                for (int i = 0; i < segments; i++) {
                    float a0 = (float) Math.toRadians(layerSpin + (360f / segments) * i);
                    float a1 = (float) Math.toRadians(layerSpin + (360f / segments) * (i + 1));

                    float x0 = (float) Math.cos(a0) * r, z0 = (float) Math.sin(a0) * r;
                    float x1 = (float) Math.cos(a1) * r, z1 = (float) Math.sin(a1) * r;

                    float y0 = yOffset + z0 * sinT;
                    float tz0 = z0 * cosT;
                    float y1 = yOffset + z1 * sinT;
                    float tz1 = z1 * cosT;

                    addVertex(consumer, pose, 0, yOffset, 0, lr, lg, lb, alpha, 0.5f, 0.5f);
                    addVertex(consumer, pose, x0, y0, tz0, lr, lg, lb, alpha * 0.1f, 0f, 0f);
                    addVertex(consumer, pose, x1, y1, tz1, lr, lg, lb, alpha * 0.1f, 1f, 0f);
                }
            }
        });
    }

    private void submitArcs(PoseStack poseStack, SubmitNodeCollector collector, Vec3 origin, long time, float[] color, float[] boltCore, boolean pointDown) {
        Random random = new Random(time / FLICKER_INTERVAL_MS);
        int boltCount = 3 + random.nextInt(2);
        float verticalSign = pointDown ? -1f : 1f;

        float phase = (time % FLICKER_INTERVAL_MS) / (float) FLICKER_INTERVAL_MS;
        float envelope = phase < 0.3f ? phase / 0.3f : 1f - (phase - 0.3f) / 0.7f;

        submitAt(poseStack, collector, RIFT_GEOMETRY, origin, (pose, consumer) -> {
            for (int i = 0; i < boltCount; i++) {
                float angle = random.nextFloat() * 360f;
                float totalLength = 0.6f + random.nextFloat() * 0.8f;
                drawLightningBolt(consumer, pose, random, 0, 0, 0, angle, totalLength, verticalSign, 6, true, color, boltCore, envelope);
            }
        });
    }

    private void drawLightningBolt(VertexConsumer consumer, PoseStack.Pose pose, Random random,
                                   float startX, float startY, float startZ,
                                   float baseAngle, float remainingLength, float verticalSign,
                                   int segments, boolean isMainBolt, float[] color, float[] boltCore, float envelope) {
        float x = startX, y = startY, z = startZ;
        float segLength = remainingLength / segments;

        for (int s = 0; s < segments; s++) {
            float jitterAngle = baseAngle + (random.nextFloat() - 0.5f) * 50f;
            float nx = x + (float) Math.cos(Math.toRadians(jitterAngle)) * segLength;
            float nz = z + (float) Math.sin(Math.toRadians(jitterAngle)) * segLength;
            float ny = y + (0.15f + random.nextFloat() * 0.25f) * segLength * verticalSign * (isMainBolt ? 1f : 0.6f);

            float progress = (float) s / segments;
            float thickness = isMainBolt ? (0.018f * (1f - progress * 0.6f)) : 0.008f;
            float alpha = (isMainBolt ? (0.9f - progress * 0.3f) : 0.5f) * envelope;

            addQuadLine(consumer, pose, x, y, z, nx, ny, nz, color[0], color[1], color[2], alpha * 0.3f, thickness * 3.5f);
            addQuadLine(consumer, pose, x, y, z, nx, ny, nz, boltCore[0], boltCore[1], boltCore[2], alpha, thickness);

            if (isMainBolt && s > 0 && s < segments - 1 && random.nextFloat() < 0.35f) {
                float branchAngle = jitterAngle + (random.nextFloat() > 0.5f ? 1 : -1) * (40f + random.nextFloat() * 40f);
                float branchLength = remainingLength * (0.25f + random.nextFloat() * 0.25f);
                drawLightningBolt(consumer, pose, random, nx, ny, nz, branchAngle, branchLength, verticalSign,
                        Math.max(2, segments / 3), false, color, boltCore, envelope);
            }

            x = nx; y = ny; z = nz;
            baseAngle = jitterAngle;
        }
    }

    private void addQuadLine(VertexConsumer consumer, PoseStack.Pose pose,
                             float x0, float y0, float z0, float x1, float y1, float z1,
                             float r, float g, float b, float alpha, float thickness) {
        float nx = -(z1 - z0), nz = (x1 - x0);
        float len = (float) Math.sqrt(nx * nx + nz * nz);
        if (len < 0.0001f) return;
        nx = nx / len * thickness;
        nz = nz / len * thickness;

        addVertex(consumer, pose, x0 - nx, y0, z0 - nz, r, g, b, alpha, 0f, 0f);
        addVertex(consumer, pose, x0 + nx, y0, z0 + nz, r, g, b, alpha, 1f, 0f);
        addVertex(consumer, pose, x1 + nx, y1, z1 + nz, r, g, b, alpha, 1f, 1f);
        addVertex(consumer, pose, x1 - nx, y1, z1 - nz, r, g, b, alpha, 0f, 1f);
    }

    private void addVertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z,
                           float r, float g, float b, float alpha, float u, float v) {
        consumer.addVertex(pose, x, y, z)
                .setColor(r, g, b, alpha)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(pose, 0f, 1f, 0f);
    }

    private void submitAt(PoseStack poseStack, SubmitNodeCollector collector, RenderType type, Vec3 renderOrigin,
                          BiConsumer<PoseStack.Pose, VertexConsumer> builder) {
        collector.submitCustomGeometry(poseStack, type, (pose, consumer) -> {
            PoseStack local = new PoseStack();
            local.translate(renderOrigin.x, renderOrigin.y, renderOrigin.z);
            builder.accept(local.last(), consumer);
        });
    }

    private void submitBeam(PoseStack poseStack, SubmitNodeCollector collector, Vec3 fromOrigin, Vec3 toOrigin, float[] color) {
        Vec3 delta = toOrigin.subtract(fromOrigin);
        double length = delta.length();
        if (length < 0.001) return;

        Vec3 dir = delta.normalize();

        collector.submitCustomGeometry(poseStack, RIFT_GEOMETRY, (pose, consumer) -> {
            float beamRadius = 0.05f;
            float time = (System.currentTimeMillis() % 10000L) / 10000.0f;
            float vOffset = -(time * 3f);

            PoseStack local = new PoseStack();
            local.translate(fromOrigin.x, fromOrigin.y, fromOrigin.z);

            Quaternionf rotation = new Quaternionf().rotationTo(
                    new Vector3f(0, 1, 0),
                    new Vector3f((float) dir.x, (float) dir.y, (float) dir.z)
            );
            local.mulPose(rotation);

            for (int face = 0; face < 4; face++) {
                local.pushPose();
                local.mulPose(Axis.YP.rotationDegrees(face * 90f));

                PoseStack.Pose facePose = local.last();

                addVertex(consumer, facePose, -beamRadius, (float) length, -beamRadius,
                        color[0], color[1], color[2], 0.55f, 0f, vOffset + (float) length);
                addVertex(consumer, facePose, beamRadius, (float) length, -beamRadius,
                        color[0], color[1], color[2], 0.55f, 1f, vOffset + (float) length);
                addVertex(consumer, facePose, beamRadius, 0f, -beamRadius,
                        color[0], color[1], color[2], 0.55f, 1f, vOffset);
                addVertex(consumer, facePose, -beamRadius, 0f, -beamRadius,
                        color[0], color[1], color[2], 0.55f, 0f, vOffset);

                local.popPose();
            }
        });
    }

    private static final RenderType RIFT_GEOMETRY = RenderType.create("rift_geometry", RenderSetup.builder(
                    RenderPipelines.BEACON_BEAM_OPAQUE)
            .withTexture("Sampler0", Identifier.withDefaultNamespace("textures/entity/beacon/beacon_beam.png"))
            .sortOnUpload()
            .createRenderSetup()
    );

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
                blockEntity.getBlockPos().above(5).north(4).east(4),
                blockEntity.getBlockPos().below(1).south(4).west(4));
    }


}