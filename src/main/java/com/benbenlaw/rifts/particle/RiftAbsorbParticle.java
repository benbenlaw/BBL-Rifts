package com.benbenlaw.rifts.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;

public class RiftAbsorbParticle extends SingleQuadParticle {

    private final double xStart;
    private final double yStart;
    private final double zStart;
    private final double xOffset;
    private final double yOffset;
    private final double zOffset;
    private final float baseSize;

    public RiftAbsorbParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, TextureAtlasSprite sprite) {
        super(level, x, y, z, sprite);
        this.xStart = x;
        this.yStart = y;
        this.zStart = z;
        this.xOffset = xd;
        this.yOffset = yd;
        this.zOffset = zd;
        this.x = this.xo = x + xd;
        this.y = this.yo = y + yd;
        this.z = this.zo = z + zd;
        this.hasPhysics = false;
        this.lifetime = 16 + this.random.nextInt(8);
        this.baseSize = 0.08F + this.random.nextFloat() * 0.06F;
        this.quadSize = baseSize;

        float brightness = 0.85F + this.random.nextFloat() * 0.15F;
        this.rCol = (0.62F + this.random.nextFloat() * 0.16F) * brightness;
        this.gCol = (0.22F + this.random.nextFloat() * 0.33F) * brightness;
        this.bCol = brightness;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        float remaining = 1.0F - (float) this.age / this.lifetime;
        float eased = remaining * remaining;
        this.x = xStart + xOffset * eased;
        this.y = yStart + yOffset * eased;
        this.z = zStart + zOffset * eased;
        this.quadSize = baseSize * (0.4F + 0.6F * remaining);
    }

    @Override
    public void move(double xa, double ya, double za) {
    }

    @Override
    public int getLightCoords(float a) {
        return LightCoordsUtil.withBlock(super.getLightCoords(a), 15);
    }

    @Override
    protected SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random) {
            return new RiftAbsorbParticle(level, x, y, z, xAux, yAux, zAux, this.sprites.get(random));
        }
    }
}
