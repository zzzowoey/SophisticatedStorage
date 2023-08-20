package net.p3pp3rf1y.sophisticatedstorage.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.p3pp3rf1y.sophisticatedstorage.init.ModBlocks;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class StorageBlockLootProvider extends FabricBlockLootTableProvider {
	public StorageBlockLootProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generate() {
		Map<Block, LootTable.Builder> tables = new HashMap<>();

		tables.put(ModBlocks.BARREL, getStorage(ModBlocks.BARREL_ITEM));
		tables.put(ModBlocks.IRON_BARREL, getStorage(ModBlocks.IRON_BARREL_ITEM));
		tables.put(ModBlocks.GOLD_BARREL, getStorage(ModBlocks.GOLD_BARREL_ITEM));
		tables.put(ModBlocks.DIAMOND_BARREL, getStorage(ModBlocks.DIAMOND_BARREL_ITEM));
		tables.put(ModBlocks.NETHERITE_BARREL, getStorage(ModBlocks.NETHERITE_BARREL_ITEM));
		tables.put(ModBlocks.LIMITED_BARREL_1, getStorage(ModBlocks.LIMITED_BARREL_1_ITEM));
		tables.put(ModBlocks.LIMITED_IRON_BARREL_1, getStorage(ModBlocks.LIMITED_IRON_BARREL_1_ITEM));
		tables.put(ModBlocks.LIMITED_GOLD_BARREL_1, getStorage(ModBlocks.LIMITED_GOLD_BARREL_1_ITEM));
		tables.put(ModBlocks.LIMITED_DIAMOND_BARREL_1, getStorage(ModBlocks.LIMITED_DIAMOND_BARREL_1_ITEM));
		tables.put(ModBlocks.LIMITED_NETHERITE_BARREL_1, getStorage(ModBlocks.LIMITED_NETHERITE_BARREL_1_ITEM));
		tables.put(ModBlocks.LIMITED_BARREL_2, getStorage(ModBlocks.LIMITED_BARREL_2_ITEM));
		tables.put(ModBlocks.LIMITED_IRON_BARREL_2, getStorage(ModBlocks.LIMITED_IRON_BARREL_2_ITEM));
		tables.put(ModBlocks.LIMITED_GOLD_BARREL_2, getStorage(ModBlocks.LIMITED_GOLD_BARREL_2_ITEM));
		tables.put(ModBlocks.LIMITED_DIAMOND_BARREL_2, getStorage(ModBlocks.LIMITED_DIAMOND_BARREL_2_ITEM));
		tables.put(ModBlocks.LIMITED_NETHERITE_BARREL_2, getStorage(ModBlocks.LIMITED_NETHERITE_BARREL_2_ITEM));
		tables.put(ModBlocks.LIMITED_BARREL_3, getStorage(ModBlocks.LIMITED_BARREL_3_ITEM));
		tables.put(ModBlocks.LIMITED_IRON_BARREL_3, getStorage(ModBlocks.LIMITED_IRON_BARREL_3_ITEM));
		tables.put(ModBlocks.LIMITED_GOLD_BARREL_3, getStorage(ModBlocks.LIMITED_GOLD_BARREL_3_ITEM));
		tables.put(ModBlocks.LIMITED_DIAMOND_BARREL_3, getStorage(ModBlocks.LIMITED_DIAMOND_BARREL_3_ITEM));
		tables.put(ModBlocks.LIMITED_NETHERITE_BARREL_3, getStorage(ModBlocks.LIMITED_NETHERITE_BARREL_3_ITEM));
		tables.put(ModBlocks.LIMITED_BARREL_4, getStorage(ModBlocks.LIMITED_BARREL_4_ITEM));
		tables.put(ModBlocks.LIMITED_IRON_BARREL_4, getStorage(ModBlocks.LIMITED_IRON_BARREL_4_ITEM));
		tables.put(ModBlocks.LIMITED_GOLD_BARREL_4, getStorage(ModBlocks.LIMITED_GOLD_BARREL_4_ITEM));
		tables.put(ModBlocks.LIMITED_DIAMOND_BARREL_4, getStorage(ModBlocks.LIMITED_DIAMOND_BARREL_4_ITEM));
		tables.put(ModBlocks.LIMITED_NETHERITE_BARREL_4, getStorage(ModBlocks.LIMITED_NETHERITE_BARREL_4_ITEM));
		tables.put(ModBlocks.CHEST, getStorage(ModBlocks.CHEST_ITEM));
		tables.put(ModBlocks.IRON_CHEST, getStorage(ModBlocks.IRON_CHEST_ITEM));
		tables.put(ModBlocks.GOLD_CHEST, getStorage(ModBlocks.GOLD_CHEST_ITEM));
		tables.put(ModBlocks.DIAMOND_CHEST, getStorage(ModBlocks.DIAMOND_CHEST_ITEM));
		tables.put(ModBlocks.NETHERITE_CHEST, getStorage(ModBlocks.NETHERITE_CHEST_ITEM));
		tables.put(ModBlocks.SHULKER_BOX, getStorage(ModBlocks.SHULKER_BOX_ITEM));
		tables.put(ModBlocks.IRON_SHULKER_BOX, getStorage(ModBlocks.IRON_SHULKER_BOX_ITEM));
		tables.put(ModBlocks.GOLD_SHULKER_BOX, getStorage(ModBlocks.GOLD_SHULKER_BOX_ITEM));
		tables.put(ModBlocks.DIAMOND_SHULKER_BOX, getStorage(ModBlocks.DIAMOND_SHULKER_BOX_ITEM));
		tables.put(ModBlocks.NETHERITE_SHULKER_BOX, getStorage(ModBlocks.NETHERITE_SHULKER_BOX_ITEM));
		tables.put(ModBlocks.CONTROLLER, createSingleItemTable(ModBlocks.CONTROLLER_ITEM));
		tables.put(ModBlocks.STORAGE_LINK, createSingleItemTable(ModBlocks.STORAGE_LINK_ITEM));

		for (Map.Entry<Block, LootTable.Builder> e : tables.entrySet()) {
			add(e.getKey(), e.getValue().setParamSet(LootContextParamSets.BLOCK));
		}
	}

	@Override
	public String getName() {
		return "SophisticatedStorage block loot tables";
	}

	private static Path getPath(Path root, ResourceLocation id) {
		return root.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
	}

	private static LootTable.Builder getStorage(Item storageItem) {
		LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(storageItem);
		LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry)
				.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
				.apply(CopyStorageDataFunction.builder());
		return LootTable.lootTable().withPool(pool);
	}

	@Override
	public LootTable.Builder createSingleItemTable(ItemLike item) {
		return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(item)));
	}
}
