package net.p3pp3rf1y.sophisticatedstorage.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;
import net.p3pp3rf1y.sophisticatedstorage.init.ModItems;
import net.p3pp3rf1y.sophisticatedstorage.item.StorageToolItem;

public class ScrolledToolMessage extends SimplePacketBase {
	private final boolean next;

	public ScrolledToolMessage(boolean next) {
		this.next = next;
	}

	public ScrolledToolMessage(FriendlyByteBuf buffer) {
			this(buffer.readBoolean());
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBoolean(next);
	}


	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();
			if (player == null) {
				return;
			}

			ItemStack stack = player.getMainHandItem();
			if (stack.getItem() == ModItems.STORAGE_TOOL) {
				StorageToolItem.cycleMode(stack, next);
			}
		});
		return true;
	}

}
