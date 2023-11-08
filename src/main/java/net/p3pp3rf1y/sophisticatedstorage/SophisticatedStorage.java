package net.p3pp3rf1y.sophisticatedstorage;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.p3pp3rf1y.sophisticatedstorage.common.CommonEventHandler;
import net.p3pp3rf1y.sophisticatedstorage.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedstorage.init.ModCompat;
import net.p3pp3rf1y.sophisticatedstorage.init.ModItems;
import net.p3pp3rf1y.sophisticatedstorage.init.ModLoot;
import net.p3pp3rf1y.sophisticatedstorage.init.ModParticles;
import net.p3pp3rf1y.sophisticatedstorage.item.CapabilityStorageWrapper;
import net.p3pp3rf1y.sophisticatedstorage.item.WoodStorageBlockItem;
import net.p3pp3rf1y.sophisticatedstorage.network.StoragePacketHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SophisticatedStorage implements ModInitializer {
	public static final String ID = "sophisticatedstorage";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	public static final CreativeModeTab CREATIVE_TAB = FabricItemGroup.builder(getRL("item_group"))
			.icon(() -> WoodStorageBlockItem.setWoodType(new ItemStack(ModBlocks.GOLD_BARREL_ITEM), WoodType.SPRUCE))
			.build();

	private final CommonEventHandler commonEventHandler = new CommonEventHandler();

	@SuppressWarnings("java:S1118") //needs to be public for mod to work
	public SophisticatedStorage() {
	}

	@Override
	public void onInitialize() {
		Config.register();

		commonEventHandler.registerHandlers();

		ModBlocks.register();
		ModItems.register();

		CapabilityStorageWrapper.register();

		ModParticles.registerParticles();
		ModLoot.registerLootFunction();

		ModCompat.initCompats();

		StoragePacketHandler.init();
	}

	public static ResourceLocation getRL(String regName) {
		return new ResourceLocation(getRegistryName(regName));
	}

	public static String getRegistryName(String regName) {
		return ID + ":" + regName;
	}
}
