package com.linngdu664.bsf.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class SpawnSnowParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected SpawnSnowParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, float sizeMultiplier, SpriteSet sprites) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.sprites = sprites;
        this.friction = 0.96F;
        this.gravity = -0.1F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.xd *= 0.0;
        this.yd *= 1.2;
        this.zd *= 0.0;
        this.xd += xSpeed;
        this.yd += ySpeed;
        this.zd += zSpeed;
        this.quadSize *= 0.75F * sizeMultiplier;
        this.lifetime = (int)(5.0F / Mth.randomBetween(this.random, 0.5F, 1.0F) * sizeMultiplier);
        this.lifetime = Math.max(this.lifetime, 1);
        this.setSpriteFromAge(sprites);
        this.hasPhysics = true;
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public int getLightColor(float partialTick) {
        return 240;
    }

    public SingleQuadParticle.FacingCameraMode getFacingCameraMode() {
        return FacingCameraMode.LOOKAT_Y;
    }

    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    public float getQuadSize(float scaleFactor) {
        return this.quadSize * Mth.clamp(((float)this.age + scaleFactor) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
    }


    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            SpawnSnowParticle spawnSnowParticle = new SpawnSnowParticle(level, x, y, z, 0, 0.2, 0, 1.5F, this.sprites);
            spawnSnowParticle.setColor((float) xSpeed, (float) ySpeed, (float) zSpeed);
            return spawnSnowParticle;
        }
    }

}
