package com.benbenlaw.rifts.entity.client;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.entity.NaturalRift;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class NaturalRiftRenderer extends EntityRenderer<NaturalRift, EntityRenderState> {

    private static final Identifier TEXTURE = Rifts.identifier("textures/entity/natural_rift.png");

    private static final float HALF_WIDTH = 0.8F;
    private static final float HALF_HEIGHT = 1.15F;
    private static final float CENTRE_Y = 1.3F;

    // The texture's disc fills 0.35 of the tile either side of centre, so sampling stays inside the tile however far it is rotated
    private static final float UV_RADIUS = 0.35F;

    public NaturalRiftRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void submit(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        double dx = camera.pos.x - state.x;
        double dz = camera.pos.z - state.z;
        double length = Math.max(1.0E-4, Math.sqrt(dx * dx + dz * dz));
        float rightX = (float) (dz / length);
        float rightZ = (float) (-dx / length);

        float time = state.ageInTicks;
        float pulse = 1.0F + 0.04F * Mth.sin(time * 0.15F);

        collector.submitCustomGeometry(poseStack, RenderTypes.entityTranslucentEmissive(TEXTURE), (pose, consumer) -> {
            quad(pose, consumer, rightX, rightZ, pulse, time * 0.06F, 1.0F);
            quad(pose, consumer, rightX, rightZ, pulse * 0.82F, -time * 0.10F, 0.55F);
        });
    }

    private static void quad(PoseStack.Pose pose, VertexConsumer consumer, float rightX, float rightZ, float scale, float angle, float alpha) {
        float cos = Mth.cos(angle);
        float sin = Mth.sin(angle);
        int argb = ((int) (alpha * 255) << 24) | 0xFFFFFF;

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
