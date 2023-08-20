package net.p3pp3rf1y.sophisticatedstorage.compat.jei;

import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.p3pp3rf1y.sophisticatedcore.Config;
import net.p3pp3rf1y.sophisticatedcore.compat.common.ClientRecipeHelper;
import net.p3pp3rf1y.sophisticatedstorage.SophisticatedStorage;
import net.p3pp3rf1y.sophisticatedstorage.crafting.SmithingStorageUpgradeRecipe;
import net.p3pp3rf1y.sophisticatedstorage.crafting.StorageTierUpgradeRecipe;
import net.p3pp3rf1y.sophisticatedstorage.item.StorageBlockItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TierUpgradeRecipesMaker {
	private TierUpgradeRecipesMaker() {}

	public static List<CraftingRecipe> getCraftingRecipes() {
		return ClientRecipeHelper.getAndTransformAvailableItemGroupRecipes(StorageTierUpgradeRecipe.REGISTERED_RECIPES, StorageTierUpgradeRecipe.class, recipe -> {
			List<CraftingRecipe> itemGroupRecipes = new ArrayList<>();
			getStorageItems(recipe).forEach(storageItem -> {
				NonNullList<Ingredient> ingredients = recipe.getIngredients();
				CraftingContainer craftinginventory = new CraftingContainer(new AbstractContainerMenu(null, -1) {
					@Override
					public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
						return ItemStack.EMPTY;
					}

					public boolean stillValid(Player playerIn) {
						return false;
					}
				}, 3, 3);
				NonNullList<Ingredient> ingredientsCopy = NonNullList.createWithCapacity(ingredients.size());
				int i = 0;
				for (Ingredient ingredient : ingredients) {
					ItemStack[] ingredientItems = ingredient.getItems();
					if (ingredientItems.length == 1) {
						if (storageItem.getItem() == ingredientItems[0].getItem()) {
							ingredientsCopy.add(i, Ingredient.of(storageItem));
							craftinginventory.setItem(i, storageItem.copy());
						} else {
							ingredientsCopy.add(i, ingredient);
							craftinginventory.setItem(i, ingredientItems[0]);
						}
					}
					i++;
				}
				ItemStack result = recipe.assemble(craftinginventory, Minecraft.getInstance().level.registryAccess());
				//noinspection ConstantConditions
				ResourceLocation id = new ResourceLocation(SophisticatedStorage.ID, "tier_upgrade_" + BuiltInRegistries.ITEM.getKey(storageItem.getItem()).getPath() + result.getOrCreateTag().toString().toLowerCase(Locale.ROOT).replaceAll("[{\",}:\s]", "_"));

				itemGroupRecipes.add(new ShapedRecipe(id, "", recipe.category(), recipe.getWidth(), recipe.getHeight(), ingredientsCopy, result));
			});
			return itemGroupRecipes;
		});
	}

	private static List<ItemStack> getStorageItems(CraftingRecipe recipe) {
		NonNullList<ItemStack> storageItems = NonNullList.create();
		for (Ingredient ingredient : recipe.getIngredients()) {
			ItemStack[] ingredientItems = ingredient.getItems();

			for (ItemStack ingredientItem : ingredientItems) {
				Item item = ingredientItem.getItem();
				if (item instanceof StorageBlockItem storageBlockItem) {
					if (Config.SERVER.enabledItems.isItemEnabled(storageBlockItem)) {
						storageItems.add(new ItemStack(storageBlockItem.getBlock()));
					}
				}
			}
		}

		return storageItems;
	}

	@SuppressWarnings("removal")
	public static List<SmithingRecipe> getSmithingRecipes() {
		return ClientRecipeHelper.getAndTransformAvailableItemGroupRecipes(SmithingStorageUpgradeRecipe.REGISTERED_RECIPES, SmithingStorageUpgradeRecipe.class, recipe -> {
			List<SmithingRecipe> itemGroupRecipes = new ArrayList<>();
			getStorageItems(recipe).forEach(storageItem -> {
				SimpleContainer container = new SimpleContainer(2);
				container.setItem(0, storageItem);
				ItemStack[] additionItems = recipe.addition.getItems();
				container.setItem(1, additionItems[0]);

				ItemStack result = recipe.assemble(container, Minecraft.getInstance().level.registryAccess());
				//noinspection ConstantConditions
				ResourceLocation id = new ResourceLocation(SophisticatedStorage.ID, "tier_upgrade_" + BuiltInRegistries.ITEM.getKey(storageItem.getItem()).getPath() + result.getOrCreateTag().toString().toLowerCase(Locale.ROOT).replaceAll("[{\",}:\s]", "_"));

				itemGroupRecipes.add(new LegacyUpgradeRecipe(id, Ingredient.of(storageItem), recipe.addition, result));
			});
			return itemGroupRecipes;
		});
	}

	@SuppressWarnings("removal")
	private static List<ItemStack> getStorageItems(LegacyUpgradeRecipe recipe) {
		NonNullList<ItemStack> storageItems = NonNullList.create();

		for (ItemStack ingredientItem : recipe.base.getItems()) {
			Item item = ingredientItem.getItem();
			if (item instanceof StorageBlockItem storageBlockItem) {
				if (Config.SERVER.enabledItems.isItemEnabled(storageBlockItem)) {
					storageItems.add(new ItemStack(storageBlockItem.getBlock()));
				}
			}
		}

		return storageItems;
	}
}
