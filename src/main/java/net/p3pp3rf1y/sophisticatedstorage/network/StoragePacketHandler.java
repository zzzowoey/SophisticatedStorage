package net.p3pp3rf1y.sophisticatedstorage.network;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.p3pp3rf1y.sophisticatedcore.network.SimplePacketBase;
import net.p3pp3rf1y.sophisticatedstorage.SophisticatedStorage;

import java.util.function.Function;

public class StoragePacketHandler {
	private static int index = 0;
	private static SimpleChannel channel;

	public static final ResourceLocation CHANNEL_NAME = SophisticatedStorage.getRL("channel");


	public static void init() {
		channel = new SimpleChannel(CHANNEL_NAME);
		channel.initServerListener();

		registerC2SMessage(OpenStorageInventoryMessage.class, OpenStorageInventoryMessage::new);
		registerC2SMessage(RequestStorageContentsMessage.class, RequestStorageContentsMessage::new);
		registerC2SMessage(ScrolledToolMessage.class, ScrolledToolMessage::new);

		registerS2CMessage(StorageContentsMessage.class, StorageContentsMessage::new);
	}
	public static <T extends SimplePacketBase> void registerC2SMessage(Class<T> type, Function<FriendlyByteBuf, T> factory) {
		getChannel().registerC2SPacket(type, index++, factory);
	}
	public static <T extends SimplePacketBase> void registerS2CMessage(Class<T> type, Function<FriendlyByteBuf, T> factory) {
		getChannel().registerS2CPacket(type, index++, factory);
	}

	public static SimpleChannel getChannel() {
		return channel;
	}

	public static void sendToServer(Object message) {
		getChannel().sendToServer((C2SPacket) message);
	}

	public static void sendToClient(ServerPlayer player, Object message) {
		getChannel().sendToClient((S2CPacket) message, player);
	}

	public static void sendToAllNear(ServerLevel world, BlockPos pos, int range, Object message) {
		getChannel().sendToClientsAround((S2CPacket) message, world, pos, range);
	}
	public static void sendToAllNear(ServerLevel world, Vec3 pos, int range, Object message) {
		getChannel().sendToClientsAround((S2CPacket) message, world, pos, range);
	}

}
