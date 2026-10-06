package com.benbenlaw.rifts.entity.client;

import com.benbenlaw.rifts.block.entity.renderer.RiftPortalRendering;
import com.benbenlaw.rifts.entity.NaturalRift;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public class NaturalRiftRenderer extends EntityRenderer<NaturalRift, EntityRenderState> {

    public NaturalRiftRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void submit(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        RiftPortalRendering.submit(poseStack, collector, camera.pos, state.x, state.y, state.z, state.ageInTicks,
                RiftPortalRendering.PORTAL, null, 1.0F);
    }
}
