package net.p3pp3rf1y.sophisticatedstorage.client.init;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.p3pp3rf1y.sophisticatedstorage.client.particle.CustomTintTerrainParticle;

import static net.p3pp3rf1y.sophisticatedstorage.init.ModParticles.TERRAIN_PARTICLE;

public class ModParticles {
	private ModParticles() {}

	@SuppressWarnings("unused") // need this to register the event correctly
	public static void registerProviders() {
		ParticleFactoryRegistry.getInstance().register(TERRAIN_PARTICLE, spriteSet -> new CustomTintTerrainParticle.Factory());
	}
}
