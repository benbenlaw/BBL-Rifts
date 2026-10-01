package com.benbenlaw.rifts.entity.client;

import com.benbenlaw.rifts.Rifts;
import com.benbenlaw.rifts.entity.RiftElemental;
import net.minecraft.client.model.animal.golem.IronGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.IronGolemRenderState;
import net.minecraft.resources.Identifier;

public class RiftElementalRenderer extends MobRenderer<RiftElemental, IronGolemRenderState, IronGolemModel> {

    private static final Identifier TEXTURE = Rifts.identifier("textures/entity/rift_elemental.png");

    public RiftElementalRenderer(EntityRendererProvider.Context context) {
        super(context, new IronGolemModel(context.bakeLayer(ModelLayers.IRON_GOLEM)), 0.7F);
    }

    @Override
    public Identifier getTextureLocation(IronGolemRenderState state) {
        return TEXTURE;
    }

    @Override
    public IronGolemRenderState createRenderState() {
        return new IronGolemRenderState();
    }

    @Override
    public void extractRenderState(RiftElemental entity, IronGolemRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.attackTicksRemaining = entity.getAttackAnimationTick() > 0 ? entity.getAttackAnimationTick() - partialTicks : 0.0F;
    }
}
