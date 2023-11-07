package net.p3pp3rf1y.sophisticatedstorage.network;

import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;
import net.p3pp3rf1y.sophisticatedstorage.block.ItemContentsStorage;

public class RequestStorageContentsMessage extends SimplePacketBase {
	private final UUID storageUuid;

	public RequestStorageContentsMessage(UUID storageUuid) {
		this.storageUuid = storageUuid;
	}

	public RequestStorageContentsMessage(FriendlyByteBuf buffer) {
		this(buffer.readUUID());
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUUID(storageUuid);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();
			if (player == null) {
				return;
			}

			StoragePacketHandler.sendToClient(player, new StorageContentsMessage(storageUuid, ItemContentsStorage.get().getOrCreateStorageContents(storageUuid)));
		});
		return true;
	}

}
