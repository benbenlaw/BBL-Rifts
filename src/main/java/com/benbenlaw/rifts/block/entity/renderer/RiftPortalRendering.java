package com.benbenlaw.rifts.block.entity.renderer;

import com.benbenlaw.rifts.Rifts;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class RiftPortalRendering {

    public static final Identifier PORTAL = Rifts.identifier("textures/entity/natural_rift.png");
    // Greyscale copy of the portal so a tint colour gives the true hue instead of multiplying the blue texture
    public static final Identifier PORTAL_TINTABLE = Rifts.identifier("textures/entity/rift_portal_tintable.png");

    private static final float HALF_WIDTH = 0.8F;
    private static final float HALF_HEIGHT = 1.15F;
    private static final float CENTRE_Y = 1.3F;

    // The texture's disc fills 0.35 of the tile either side of centre, so sampling stays inside the tile however far it is rotated
    private static final float UV_RADIUS = 0.35F;

    private static final float[] WHITE = {1.0F, 1.0F, 1.0F};

    public static void submit(PoseStack poseStack, SubmitNodeCollector collector, Vec3 camera, double x, double y, double z,
                              float ageTicks, Identifier texture, float[] tint, float alpha) {
        double dx = camera.x - x;
        double dz = camera.z - z;
        double length = Math.max(1.0E-4, Math.sqrt(dx * dx + dz * dz));
        float rightX = (float) (dz / length);
        float rightZ = (float) (-dx / length);
        float pulse = 1.0F + 0.04F * Mth.sin(ageTicks * 0.15F);
        float[] colour = tint == null ? WHITE : tint;

        collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucentEmissive(texture), (pose, consumer) -> {
            PoseStack local = new PoseStack();
            local.translate(x - camera.x, y - camera.y, z - camera.z);
            PoseStack.Pose localPose = local.last();

            quad(localPose, consumer, rightX, rightZ, pulse, ageTicks * 0.06F, colour, alpha);
            quad(localPose, consumer, rightX, rightZ, pulse * 0.82F, -ageTicks * 0.10F, colour, alpha * 0.55F);
        });
    }

    private static void quad(PoseStack.Pose pose, VertexConsumer consumer, float rightX, float rightZ, float scale, float angle, float[] colour, float alpha) {
        float cos = Mth.cos(angle);
        float sin = Mth.sin(angle);
        int argb = ((int) (Mth.clamp(alpha, 0.0F, 1.0F) * 255) << 24)
                | ((int) (colour[0] * 255) << 16) | ((int) (colour[1] * 255) << 8) | (int) (colour[2] * 255);

        float[][] corners = {{-1, -1}, {1, -1}, {1, 1}, {-1, 1}};
        for (int pass = 0; pass < 2; pass++) {
            for (int i = 0; i < 4; i++) {
                float[] corner = corners[pass == 0 ? i : 3 - i];
                float u = UV_RADIUS * (corner[0] * cos - corner[1] * sin) + 0.5F;
                float v = UV_RADIUS * (corner[0] * sin + corner[1] * cos) + 0.5F;
                float side = corner[0] * HALF_WIDTH * scale;
                consumer.addVertex(pose, rightX * side, CENTRE_Y + corner[1] * HALF_HEIGHT * scale, rightZ * side)
                        .setColor(argb)
                        .setUv(u, v)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(0xF000F0)
                        .setNormal(pose, 0.0F, 1.0F, 0.0F);
            }
        }
    }
}
