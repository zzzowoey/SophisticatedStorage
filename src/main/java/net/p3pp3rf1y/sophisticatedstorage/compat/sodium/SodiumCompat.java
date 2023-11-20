package net.p3pp3rf1y.sophisticatedstorage.compat.sodium;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import net.fabricmc.api.EnvType;
import net.p3pp3rf1y.sophisticatedcore.compat.ICompat;

public class SodiumCompat implements ICompat {
	@Override
	public void setup() {
		EnvExecutor.runWhenOn(EnvType.CLIENT, () -> SodiumTranslucentVertexConsumer::register);
	}
}
