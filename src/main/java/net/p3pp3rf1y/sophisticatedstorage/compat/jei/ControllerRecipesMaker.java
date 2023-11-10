package net.p3pp3rf1y.sophisticatedstorage.compat.jei;

import com.google.common.base.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.p3pp3rf1y.sophisticatedcore.Config;
import net.p3pp3rf1y.sophisticatedcore.compat.common.ClientRecipeHelper;
import net.p3pp3rf1y.sophisticatedstorage.init.ModBlocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ControllerRecipesMaker {
	private ControllerRecipesMaker() {}

	public static List<CraftingRecipe> getRecipes() {
		return ClientRecipeHelper.getRecipeByKey(BuiltInRegistries.ITEM.getKey(ModBlocks.CONTROLLER_ITEM)).map((Function<Recipe<?>, List<CraftingRecipe>>) r -> {
			if (!(r instanceof ShapedRecipe originalRecipe)) {
				return new ArrayList<>();
			}

			NonNullList<Ingredient> ingredients = r.getIngredients();
			NonNullList<Ingredient> ingredientsCopy = NonNullList.createWithCapacity(ingredients.size());
			int i = 0;
			for (Ingredient ingredient : ingredients) {
				ingredientsCopy.add(i, getExpandedIngredient(ingredient));
				i++;
			}
			// TODO: Check
			return Collections.singletonList(new ShapedRecipe(originalRecipe.getId(), "", originalRecipe.category(), originalRecipe.getWidth(), originalRecipe.getHeight(), ingredientsCopy, originalRecipe.getResultItem(Minecraft.getInstance().level.registryAccess())));
		}).orElse(Collections.emptyList());
	}

	private static Ingredient getExpandedIngredient(Ingredient ingredient) {
		ItemStack[] ingredientItems = ingredient.getItems();

		NonNullList<ItemStack> storages = NonNullList.create();
		for (ItemStack ingredientItem : ingredientItems) {
			Item item = ingredientItem.getItem();
			if (item instanceof BlockItem itemBase && (item == ModBlocks.BARREL_ITEM || item == ModBlocks.CHEST_ITEM)) {
				if (Config.COMMON.enabledItems.isItemEnabled(itemBase)) {
					storages.add(new ItemStack(itemBase.getBlock()));
				}
			}
		}
		if (!storages.isEmpty()) {
			return Ingredient.of(storages.toArray(new ItemStack[0]));
		} else {
			return ingredient;
		}
	}
}
