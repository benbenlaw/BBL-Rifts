package com.benbenlaw.rifts.entity.client;

import com.benbenlaw.rifts.block.entity.renderer.RiftPortalRendering;
import com.benbenlaw.rifts.entity.RiftPortal;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class RiftPortalRenderer extends EntityRenderer<RiftPortal, RiftPortalRenderer.State> {

    private static final float UV_RADIUS = 0.35F;

    public RiftPortalRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public static class State extends EntityRenderState {
        public Direction facing = Direction.NORTH;
        public Direction longAxis = Direction.UP;
        public int colour;
    }

    private static int brighten(int channel) {
        return Math.min(255, channel + (255 - channel) / 4);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(RiftPortal entity, State state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.facing = entity.facing();
        state.longAxis = entity.longAxis();
        int diffuse = entity.color().getTextureDiffuseColor();
        int r = brighten((diffuse >> 16) & 0xFF);
        int g = brighten((diffuse >> 8) & 0xFF);
        int b = brighten(diffuse & 0xFF);
        state.colour = (r << 16) | (g << 8) | b;
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        Vec3 n = new Vec3(state.facing.getStepX(), state.facing.getStepY(), state.facing.getStepZ());
        Vec3 up = new Vec3(state.longAxis.getStepX(), state.longAxis.getStepY(), state.longAxis.getStepZ());
        Vec3 right = new Vec3(up.y * n.z - up.z * n.y, up.z * n.x - up.x * n.z, up.x * n.y - up.y * n.x);
        int colour = state.colour;
        float age = state.ageInTicks;
        float pulse = 1.0F + 0.03F * Mth.sin(age * 0.15F);

        double x = state.x - camera.pos.x;
        double y = state.y - camera.pos.y;
        double z = state.z - camera.pos.z;

        collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucentEmissive(RiftPortalRendering.PORTAL_TINTABLE), (pose, consumer) -> {
            PoseStack local = new PoseStack();
            local.translate(x, y, z);
            PoseStack.Pose localPose = local.last();

            quad(localPose, consumer, n, up, right, 0.0F, pulse, age * 0.06F, colour, 1.0F);
            quad(localPose, consumer, n, up, right, 0.01F, pulse * 0.82F, -age * 0.10F, colour, 0.55F);
        });
    }

    private static void quad(PoseStack.Pose pose, VertexConsumer consumer, Vec3 n, Vec3 up, Vec3 right, float lift, float scale, float angle, int rgb, float alpha) {
        float cos = Mth.cos(angle);
        float sin = Mth.sin(angle);
        int argb = ((int) (alpha * 255) << 24) | (rgb & 0xFFFFFF);

        float[][] corners = {{-1, -1}, {1, -1}, {1, 1}, {-1, 1}};
        for (int pass = 0; pass < 2; pass++) {
            for (int i = 0; i < 4; i++) {
                float[] corner = corners[pass == 0 ? i : 3 - i];
                float u = UV_RADIUS * (corner[0] * cos - corner[1] * sin) + 0.5F;
                float v = UV_RADIUS * (corner[0] * sin + corner[1] * cos) + 0.5F;
                Vec3 vertex = right.scale(corner[0] * 0.5 * scale).add(up.scale(corner[1] * 1.0 * scale)).add(n.scale(lift));
                consumer.addVertex(pose, (float) vertex.x, (float) vertex.y, (float) vertex.z)
                        .setColor(argb)
                        .setUv(u, v)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(0xF000F0)
                        .setNormal(pose, (float) n.x, (float) n.y, (float) n.z);
            }
        }
    }
}
