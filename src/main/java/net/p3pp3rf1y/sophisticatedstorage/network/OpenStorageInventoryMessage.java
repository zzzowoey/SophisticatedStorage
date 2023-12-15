package net.p3pp3rf1y.sophisticatedstorage.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;
import net.p3pp3rf1y.sophisticatedcore.util.MenuProviderHelper;
import net.p3pp3rf1y.sophisticatedcore.util.WorldHelper;
import net.p3pp3rf1y.sophisticatedstorage.block.LimitedBarrelBlock;
import net.p3pp3rf1y.sophisticatedstorage.block.StorageBlockEntity;
import net.p3pp3rf1y.sophisticatedstorage.common.gui.LimitedBarrelContainerMenu;
import net.p3pp3rf1y.sophisticatedstorage.common.gui.StorageContainerMenu;

public class OpenStorageInventoryMessage extends SimplePacketBase {
	private final BlockPos pos;

	public OpenStorageInventoryMessage(BlockPos pos) {this.pos = pos;}

	public OpenStorageInventoryMessage(FriendlyByteBuf buffer) {
		this(buffer.readBlockPos());
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
	}

	@Override
	public boolean handle(Context context) {
		context.enqueueWork(() -> {
			ServerPlayer player = context.getSender();
			if (player == null) {
				return;
			}

			player.openMenu(
					MenuProviderHelper.createMenuProvider(
							(w, ctx, pl) -> {
								if (pl.level().getBlockState(pos).getBlock() instanceof LimitedBarrelBlock) {
									return new LimitedBarrelContainerMenu(w, pl, pos);
								} else {
									return new StorageContainerMenu(w, pl, pos);
								}
							},
							buffer -> buffer.writeBlockPos(pos),
							WorldHelper.getBlockEntity(player.level(), pos, StorageBlockEntity.class).map(StorageBlockEntity::getDisplayName).orElse(Component.empty())
					)
			);
		});
		return true;
	}

}
