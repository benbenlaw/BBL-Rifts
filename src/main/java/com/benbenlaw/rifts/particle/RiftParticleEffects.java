package com.benbenlaw.rifts.particle;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

public class RiftParticleEffects {

    // Count 0 makes the client treat (dx, dy, dz) as the particle's offset, so each mote flies in from that offset to the centre
    public static void absorb(ServerLevel level, double x, double y, double z, int count, double minDistance, double maxDistance, double verticalSpread) {
        RandomSource random = level.getRandom();
        for (int i = 0; i < count; i++) {
            double theta = random.nextDouble() * Math.PI * 2;
            double distance = minDistance + random.nextDouble() * (maxDistance - minDistance);
            double dy = (random.nextDouble() - 0.5) * 2 * verticalSpread;

            level.sendParticles(RiftsParticles.RIFT_ABSORB.get(), x, y, z, 0,
                    Math.cos(theta) * distance, dy, Math.sin(theta) * distance, 1.0);
        }
    }
}
