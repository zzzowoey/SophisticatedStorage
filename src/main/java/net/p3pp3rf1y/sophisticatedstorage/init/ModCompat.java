package net.p3pp3rf1y.sophisticatedstorage.init;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.version.VersionInterval;
import net.fabricmc.loader.impl.util.version.VersionIntervalImpl;
import net.p3pp3rf1y.sophisticatedcore.compat.ICompat;
import net.p3pp3rf1y.sophisticatedstorage.SophisticatedStorage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class ModCompat {
	private ModCompat() {}

	// private static final String RUBIDIUM_MOD_ID = "rubidium";

	private static final Map<CompatInfo, Supplier<Callable<ICompat>>> compatFactories = new HashMap<>();

	static {
		// compatFactories.put(new CompatInfo(CompatModIds.QUARK, null), () -> QuarkCompat::new);
		// compatFactories.put(new CompatInfo(RUBIDIUM_MOD_ID, fromSpec("[0.6.5]")), () -> RubidiumCompat::new);
	}

	public static void initCompats() {
		for (Map.Entry<CompatInfo, Supplier<Callable<ICompat>>> entry : compatFactories.entrySet()) {
			if (entry.getKey().isLoaded()) {
				try {
					entry.getValue().get().call().setup();
				}
				catch (Exception e) {
					SophisticatedStorage.LOGGER.error("Error instantiating compatibility ", e);
				}
			}
		}
	}

	record CompatInfo(String modId, @Nullable VersionInterval supportedVersionRange){
		public boolean isLoaded() {
			return FabricLoader.getInstance().getModContainer(modId())
					.map(container -> supportedVersionRange() == null || !VersionInterval.and(Collections.singletonList(supportedVersionRange()), Collections.singletonList(new VersionIntervalImpl(container.getMetadata().getVersion(), true, container.getMetadata().getVersion(), true))).isEmpty())
					.orElse(false);
		}
	}
}
