package net.p3pp3rf1y.sophisticatedstorage.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.p3pp3rf1y.sophisticatedcore.client.render.ClientStorageContentsTooltip;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;
import net.p3pp3rf1y.sophisticatedstorage.block.ItemContentsStorage;

import javax.annotation.Nullable;
import java.util.UUID;

public class StorageContentsMessage extends SimplePacketBase {
	private final UUID shulkerBoxUuid;
	@Nullable
	private final CompoundTag contents;

	public StorageContentsMessage(UUID shulkerBoxUuid, @Nullable CompoundTag contents) {
		this.shulkerBoxUuid = shulkerBoxUuid;
		this.contents = contents;
	}

	public StorageContentsMessage(FriendlyByteBuf buffer) {
		this(buffer.readUUID(), buffer.readNbt());
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeUUID(shulkerBoxUuid);
		buffer.writeNbt(contents);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			LocalPlayer player = Minecraft.getInstance().player;
			if (player == null || contents == null) {
				return;
			}

			ItemContentsStorage.get().setStorageContents(shulkerBoxUuid, contents);
			ClientStorageContentsTooltip.refreshContents();
		});
		return true;
	}

}
