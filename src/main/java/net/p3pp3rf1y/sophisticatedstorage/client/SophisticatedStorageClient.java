package net.p3pp3rf1y.sophisticatedstorage.client;

import net.fabricmc.api.ClientModInitializer;
import net.p3pp3rf1y.sophisticatedstorage.network.StoragePacketHandler;

public class SophisticatedStorageClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientEventHandler.registerHandlers();

        StoragePacketHandler.getChannel().initClientListener();
    }
}
