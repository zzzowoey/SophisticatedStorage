package net.p3pp3rf1y.sophisticatedstorage.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedcore.crafting.IWrapperRecipe;
import net.p3pp3rf1y.sophisticatedcore.crafting.RecipeWrapperSerializer;
import net.p3pp3rf1y.sophisticatedstorage.block.IStorageBlock;
import net.p3pp3rf1y.sophisticatedstorage.common.CapabilityStorageWrapper;
import net.p3pp3rf1y.sophisticatedstorage.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedstorage.item.ShulkerBoxItem;
import net.p3pp3rf1y.sophisticatedstorage.item.WoodStorageBlockItem;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class StorageTierUpgradeRecipe extends ShapedRecipe implements IWrapperRecipe<ShapedRecipe> {
	public static final Set<ResourceLocation> REGISTERED_RECIPES = new LinkedHashSet<>();
	private final ShapedRecipe compose;

	public StorageTierUpgradeRecipe(ShapedRecipe compose) {
		super(compose.getId(), compose.getGroup(), compose.category(), compose.getWidth(), compose.getHeight(), compose.getIngredients(), compose.result);
		this.compose = compose;
		REGISTERED_RECIPES.add(compose.getId());
	}

	@Override
	public boolean matches(CraftingContainer pInv, Level pLevel) {
		return super.matches(pInv, pLevel) && getOriginalStorage(pInv).map(storage -> !(storage.getItem() instanceof WoodStorageBlockItem) || !WoodStorageBlockItem.isPacked(storage)).orElse(false);
	}

	@Override
	public ShapedRecipe getCompose() {
		return compose;
	}

	@Override
	public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
		ItemStack upgradedStorage = super.assemble(inv, registryAccess);
		getOriginalStorage(inv).ifPresent(originalStorage -> upgradedStorage.setTag(originalStorage.getTag()));
		if (upgradedStorage.getItem() instanceof ShulkerBoxItem shulkerBoxItem) {
			CapabilityStorageWrapper.get(upgradedStorage).ifPresent(wrapper -> {
				shulkerBoxItem.setNumberOfInventorySlots(upgradedStorage, wrapper.getDefaultNumberOfInventorySlots());
				shulkerBoxItem.setNumberOfUpgradeSlots(upgradedStorage, wrapper.getDefaultNumberOfUpgradeSlots());
			});
		}
		return upgradedStorage;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	private Optional<ItemStack> getOriginalStorage(CraftingContainer inv) {
		for (int slot = 0; slot < inv.getContainerSize(); slot++) {
			ItemStack slotStack = inv.getItem(slot);
			if (slotStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof IStorageBlock) {
				return Optional.of(slotStack);
			}
		}

		return Optional.empty();
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModBlocks.STORAGE_TIER_UPGRADE_RECIPE_SERIALIZER;
	}

	public static class Serializer extends RecipeWrapperSerializer<ShapedRecipe, StorageTierUpgradeRecipe> {
		public Serializer() {
			super(StorageTierUpgradeRecipe::new, RecipeSerializer.SHAPED_RECIPE);
		}
	}
}
