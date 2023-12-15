package net.p3pp3rf1y.sophisticatedstorage.compat.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;

import net.minecraft.world.item.crafting.CraftingRecipe;
import net.p3pp3rf1y.sophisticatedcore.client.gui.SettingsScreen;
import net.p3pp3rf1y.sophisticatedcore.compat.emi.EmiGridMenuInfo;
import net.p3pp3rf1y.sophisticatedcore.compat.emi.EmiSettingsGhostDragDropHandler;
import net.p3pp3rf1y.sophisticatedcore.compat.emi.EmiStorageGhostDragDropHandler;
import net.p3pp3rf1y.sophisticatedstorage.client.gui.StorageScreen;
import net.p3pp3rf1y.sophisticatedstorage.client.gui.StorageSettingsScreen;
import net.p3pp3rf1y.sophisticatedstorage.common.CapabilityStorageWrapper;
import net.p3pp3rf1y.sophisticatedstorage.compat.jei.ControllerRecipesMaker;
import net.p3pp3rf1y.sophisticatedstorage.compat.jei.DyeRecipesMaker;
import net.p3pp3rf1y.sophisticatedstorage.compat.jei.FlatBarrelRecipesMaker;
import net.p3pp3rf1y.sophisticatedstorage.compat.jei.ShulkerBoxFromChestRecipesMaker;
import net.p3pp3rf1y.sophisticatedstorage.compat.jei.TierUpgradeRecipesMaker;
import net.p3pp3rf1y.sophisticatedstorage.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedstorage.init.ModItems;

import java.util.Arrays;
import java.util.Collection;

public class EmiCompat implements EmiPlugin {
	@Override
	public void register(EmiRegistry registry) {
		registry.addExclusionArea(StorageScreen.class, (screen, consumer) -> {
			screen.getUpgradeSlotsRectangle().ifPresent(r -> consumer.accept(new Bounds(r.getX(), r.getY(), r.getWidth(), r.getHeight())));
			screen.getUpgradeSettingsControl().getTabRectangles().forEach(r -> consumer.accept(new Bounds(r.getX(), r.getY(), r.getWidth(), r.getHeight())));
			screen.getSortButtonsRectangle().ifPresent(r -> consumer.accept(new Bounds(r.getX(), r.getY(), r.getWidth(), r.getHeight())));
		});

		registry.addExclusionArea(StorageSettingsScreen.class, (screen, consumer) -> {
			//noinspection ConstantValue
			if (screen == null || screen.getSettingsTabControl() == null) { // Due to how Emi collects the exclusion area this can be null
				return;
			}
			screen.getSettingsTabControl().getTabRectangles().forEach(r -> consumer.accept(new Bounds(r.getX(), r.getY(), r.getWidth(), r.getHeight())));
		});

		registry.addDragDropHandler(StorageScreen.class, new EmiStorageGhostDragDropHandler<>());
		registry.addDragDropHandler(SettingsScreen.class, new EmiSettingsGhostDragDropHandler<>());

//		registerCraftingRecipes(registry, ControllerRecipesMaker.getRecipes());
		registerCraftingRecipes(registry, DyeRecipesMaker.getRecipes());
		registerCraftingRecipes(registry, FlatBarrelRecipesMaker.getRecipes());
		registerCraftingRecipes(registry, ShulkerBoxFromChestRecipesMaker.getRecipes());
		registerCraftingRecipes(registry, TierUpgradeRecipesMaker.getShapedCraftingRecipes());

		Comparison compareColor = Comparison.of((a, b) ->
				CapabilityStorageWrapper.get(a.getItemStack())
						.map(stackA -> CapabilityStorageWrapper.get(b.getItemStack())
								.map(stackB -> stackA.getMainColor() == stackB.getMainColor() && stackA.getAccentColor() == stackB.getAccentColor())
								.orElse(false))
						.orElse(false));

		Arrays.asList(
				ModBlocks.BARREL_ITEM, ModBlocks.IRON_BARREL_ITEM, ModBlocks.GOLD_BARREL_ITEM, ModBlocks.DIAMOND_BARREL_ITEM, ModBlocks.NETHERITE_BARREL_ITEM,
				ModBlocks.LIMITED_BARREL_1_ITEM, ModBlocks.LIMITED_IRON_BARREL_1_ITEM, ModBlocks.LIMITED_GOLD_BARREL_1_ITEM, ModBlocks.LIMITED_DIAMOND_BARREL_1_ITEM, ModBlocks.LIMITED_NETHERITE_BARREL_1_ITEM,
				ModBlocks.LIMITED_BARREL_2_ITEM, ModBlocks.LIMITED_IRON_BARREL_2_ITEM, ModBlocks.LIMITED_GOLD_BARREL_2_ITEM, ModBlocks.LIMITED_DIAMOND_BARREL_2_ITEM, ModBlocks.LIMITED_NETHERITE_BARREL_2_ITEM,
				ModBlocks.LIMITED_BARREL_3_ITEM, ModBlocks.LIMITED_IRON_BARREL_3_ITEM, ModBlocks.LIMITED_GOLD_BARREL_3_ITEM, ModBlocks.LIMITED_DIAMOND_BARREL_3_ITEM, ModBlocks.LIMITED_NETHERITE_BARREL_3_ITEM,
				ModBlocks.LIMITED_BARREL_4_ITEM, ModBlocks.LIMITED_IRON_BARREL_4_ITEM, ModBlocks.LIMITED_GOLD_BARREL_4_ITEM, ModBlocks.LIMITED_DIAMOND_BARREL_4_ITEM, ModBlocks.LIMITED_NETHERITE_BARREL_4_ITEM,
				ModBlocks.CHEST_ITEM, ModBlocks.IRON_CHEST_ITEM, ModBlocks.GOLD_CHEST_ITEM, ModBlocks.DIAMOND_CHEST_ITEM, ModBlocks.NETHERITE_CHEST_ITEM
		).forEach(blockItem -> registry.setDefaultComparison(EmiStack.of(blockItem), compareColor));

		registry.addRecipeHandler(ModBlocks.STORAGE_CONTAINER_TYPE, new EmiGridMenuInfo<>());
		registry.addRecipeHandler(ModBlocks.LIMITED_BARREL_CONTAINER_TYPE, new EmiGridMenuInfo<>());
	}

	private static void registerCraftingRecipes(EmiRegistry registry, Collection<CraftingRecipe> recipes) {
		recipes.forEach(r -> registry.addRecipe(
						new EmiCraftingRecipe(
								r.getIngredients().stream().map(EmiIngredient::of).toList(),
								EmiStack.of(r.getResultItem(null)),
								r.getId())
				)
		);
	}
}
