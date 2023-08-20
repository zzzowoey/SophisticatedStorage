package net.p3pp3rf1y.sophisticatedstorage.data;

import me.alphamode.forgetags.Tags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.p3pp3rf1y.sophisticatedstorage.init.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class BlockTagProvider extends FabricTagProvider.BlockTagProvider {

	public BlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider arg) {
		getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE).add(
				ModBlocks.BARREL, ModBlocks.IRON_BARREL, ModBlocks.GOLD_BARREL, ModBlocks.DIAMOND_BARREL, ModBlocks.NETHERITE_BARREL,
				ModBlocks.LIMITED_BARREL_1, ModBlocks.LIMITED_IRON_BARREL_1, ModBlocks.LIMITED_GOLD_BARREL_1, ModBlocks.LIMITED_DIAMOND_BARREL_1, ModBlocks.LIMITED_NETHERITE_BARREL_1,
				ModBlocks.LIMITED_BARREL_2, ModBlocks.LIMITED_IRON_BARREL_2, ModBlocks.LIMITED_GOLD_BARREL_2, ModBlocks.LIMITED_DIAMOND_BARREL_2, ModBlocks.LIMITED_NETHERITE_BARREL_2,
				ModBlocks.LIMITED_BARREL_3, ModBlocks.LIMITED_IRON_BARREL_3, ModBlocks.LIMITED_GOLD_BARREL_3, ModBlocks.LIMITED_DIAMOND_BARREL_3, ModBlocks.LIMITED_NETHERITE_BARREL_3,
				ModBlocks.LIMITED_BARREL_4, ModBlocks.LIMITED_IRON_BARREL_4, ModBlocks.LIMITED_GOLD_BARREL_4, ModBlocks.LIMITED_DIAMOND_BARREL_4, ModBlocks.LIMITED_NETHERITE_BARREL_4,
				ModBlocks.CHEST, ModBlocks.IRON_CHEST, ModBlocks.GOLD_CHEST, ModBlocks.DIAMOND_CHEST, ModBlocks.NETHERITE_CHEST
		);
		getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE).add(
				ModBlocks.SHULKER_BOX, ModBlocks.IRON_SHULKER_BOX, ModBlocks.GOLD_SHULKER_BOX, ModBlocks.DIAMOND_SHULKER_BOX, ModBlocks.NETHERITE_SHULKER_BOX, ModBlocks.CONTROLLER, ModBlocks.STORAGE_LINK
		);
		getOrCreateTagBuilder(BlockTags.GUARDED_BY_PIGLINS).add(
				ModBlocks.BARREL, ModBlocks.IRON_BARREL, ModBlocks.GOLD_BARREL, ModBlocks.DIAMOND_BARREL, ModBlocks.NETHERITE_BARREL,
				ModBlocks.LIMITED_BARREL_1, ModBlocks.LIMITED_IRON_BARREL_1, ModBlocks.LIMITED_GOLD_BARREL_1, ModBlocks.LIMITED_DIAMOND_BARREL_1, ModBlocks.LIMITED_NETHERITE_BARREL_1,
				ModBlocks.LIMITED_BARREL_2, ModBlocks.LIMITED_IRON_BARREL_2, ModBlocks.LIMITED_GOLD_BARREL_2, ModBlocks.LIMITED_DIAMOND_BARREL_2, ModBlocks.LIMITED_NETHERITE_BARREL_2,
				ModBlocks.LIMITED_BARREL_3, ModBlocks.LIMITED_IRON_BARREL_3, ModBlocks.LIMITED_GOLD_BARREL_3, ModBlocks.LIMITED_DIAMOND_BARREL_3, ModBlocks.LIMITED_NETHERITE_BARREL_3,
				ModBlocks.LIMITED_BARREL_4, ModBlocks.LIMITED_IRON_BARREL_4, ModBlocks.LIMITED_GOLD_BARREL_4, ModBlocks.LIMITED_DIAMOND_BARREL_4, ModBlocks.LIMITED_NETHERITE_BARREL_4,
				ModBlocks.CHEST, ModBlocks.IRON_CHEST, ModBlocks.GOLD_CHEST, ModBlocks.DIAMOND_CHEST, ModBlocks.NETHERITE_CHEST,
				ModBlocks.SHULKER_BOX, ModBlocks.IRON_SHULKER_BOX, ModBlocks.GOLD_SHULKER_BOX, ModBlocks.DIAMOND_SHULKER_BOX, ModBlocks.NETHERITE_SHULKER_BOX
		);
		getOrCreateTagBuilder(Tags.Blocks.CHESTS).add(ModBlocks.CHEST, ModBlocks.IRON_CHEST, ModBlocks.GOLD_CHEST, ModBlocks.DIAMOND_CHEST, ModBlocks.NETHERITE_CHEST);
		getOrCreateTagBuilder(Tags.Blocks.CHESTS_WOODEN).add(ModBlocks.CHEST);
		getOrCreateTagBuilder(Tags.Blocks.BARRELS).add(ModBlocks.BARREL, ModBlocks.IRON_BARREL, ModBlocks.GOLD_BARREL, ModBlocks.DIAMOND_BARREL, ModBlocks.NETHERITE_BARREL);
		getOrCreateTagBuilder(Tags.Blocks.BARRELS_WOODEN).add(ModBlocks.BARREL);
	}
}
