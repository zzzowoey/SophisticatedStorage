package net.p3pp3rf1y.sophisticatedstorage.item;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedstorage.SophisticatedStorage;
import net.p3pp3rf1y.sophisticatedstorage.block.StorageWrapper;
import net.p3pp3rf1y.sophisticatedstorage.init.ModBlocks;

import java.util.Optional;

public class CapabilityStorageWrapper {
	public static final ItemApiLookup<StorageWrapper, Void> STORAGE_WRAPPER_CAPABILITY = ItemApiLookup.get(SophisticatedStorage.getRL("storage_wrapper"), StorageWrapper.class, Void.class);

	public static Optional<StorageWrapper> get(ItemStack provider) {
		return Optional.ofNullable(STORAGE_WRAPPER_CAPABILITY.find(provider, null));
	}

	private CapabilityStorageWrapper() {}

	public static void register() {
		STORAGE_WRAPPER_CAPABILITY.registerForItems((stack, c) -> new WoodStorageBlockItem.Wrapper(stack),
				ModBlocks.BARREL_ITEM, ModBlocks.IRON_BARREL_ITEM, ModBlocks.GOLD_BARREL_ITEM, ModBlocks.DIAMOND_BARREL_ITEM, ModBlocks.NETHERITE_BARREL_ITEM,
				ModBlocks.LIMITED_BARREL_1_ITEM, ModBlocks.LIMITED_IRON_BARREL_1_ITEM, ModBlocks.LIMITED_GOLD_BARREL_1_ITEM, ModBlocks.LIMITED_DIAMOND_BARREL_1_ITEM, ModBlocks.LIMITED_NETHERITE_BARREL_1_ITEM,
				ModBlocks.LIMITED_BARREL_2_ITEM, ModBlocks.LIMITED_IRON_BARREL_2_ITEM, ModBlocks.LIMITED_GOLD_BARREL_2_ITEM, ModBlocks.LIMITED_DIAMOND_BARREL_2_ITEM, ModBlocks.LIMITED_NETHERITE_BARREL_2_ITEM,
				ModBlocks.LIMITED_BARREL_3_ITEM, ModBlocks.LIMITED_IRON_BARREL_3_ITEM, ModBlocks.LIMITED_GOLD_BARREL_3_ITEM, ModBlocks.LIMITED_DIAMOND_BARREL_3_ITEM, ModBlocks.LIMITED_NETHERITE_BARREL_3_ITEM,
				ModBlocks.LIMITED_BARREL_4_ITEM, ModBlocks.LIMITED_IRON_BARREL_4_ITEM, ModBlocks.LIMITED_GOLD_BARREL_4_ITEM, ModBlocks.LIMITED_DIAMOND_BARREL_4_ITEM, ModBlocks.LIMITED_NETHERITE_BARREL_4_ITEM,
				ModBlocks.CHEST_ITEM, ModBlocks.IRON_CHEST_ITEM, ModBlocks.GOLD_CHEST_ITEM, ModBlocks.DIAMOND_CHEST_ITEM, ModBlocks.NETHERITE_CHEST_ITEM
		);

		STORAGE_WRAPPER_CAPABILITY.registerForItems((stack, c) -> new ShulkerBoxItem.Wrapper(stack),
				ModBlocks.SHULKER_BOX_ITEM, ModBlocks.IRON_SHULKER_BOX_ITEM, ModBlocks.GOLD_SHULKER_BOX_ITEM, ModBlocks.DIAMOND_SHULKER_BOX_ITEM, ModBlocks.NETHERITE_SHULKER_BOX_ITEM);
	}
}
