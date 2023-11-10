package net.p3pp3rf1y.sophisticatedstorage.upgrades.hopper;

import com.google.common.collect.MapMaker;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.settings.memory.MemorySettingsCategory;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilterLogic;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogic;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ITickableUpgrade;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeWrapperBase;
import net.p3pp3rf1y.sophisticatedcore.util.NBTHelper;
import net.p3pp3rf1y.sophisticatedstorage.block.StorageBlockBase;
import net.p3pp3rf1y.sophisticatedstorage.block.VerticalFacing;
import net.p3pp3rf1y.sophisticatedstorage.common.gui.BlockSide;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public class HopperUpgradeWrapper extends UpgradeWrapperBase<HopperUpgradeWrapper, HopperUpgradeItem>
		implements ITickableUpgrade {

	private Set<Direction> pullDirections = new LinkedHashSet<>();
	private Set<Direction> pushDirections = new LinkedHashSet<>();
	private final Map<Direction, BlockApiCache<Storage<ItemVariant>, Direction>> handlerCache = new MapMaker().weakKeys().weakValues().makeMap();

	private final ContentsFilterLogic inputFilterLogic;
	private final ContentsFilterLogic outputFilterLogic;
	private long coolDownTime = 0;

	protected HopperUpgradeWrapper(IStorageWrapper storageWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
		super(storageWrapper, upgrade, upgradeSaveHandler);
		inputFilterLogic = new ContentsFilterLogic(upgrade, upgradeSaveHandler, upgradeItem.getInputFilterSlotCount(), storageWrapper::getInventoryHandler,
				storageWrapper.getSettingsHandler().getTypeCategory(MemorySettingsCategory.class), "inputFilter");
		outputFilterLogic = new ContentsFilterLogic(upgrade, upgradeSaveHandler, upgradeItem.getOutputFilterSlotCount(), storageWrapper::getInventoryHandler,
				storageWrapper.getSettingsHandler().getTypeCategory(MemorySettingsCategory.class), "outputFilter");

		deserialize();
	}

	@Override
	public void tick(@Nullable LivingEntity entity, Level level, BlockPos pos) {
		initDirections(level, pos);

		if (coolDownTime > level.getGameTime()) {
			return;
		}

		for (Direction pushDirection : pushDirections) {
			if (getItemHandler(level, pos, pushDirection).map(this::pushItems)
					/*.orElse(getWorldlyContainer(level, pos, pushDirection).map(container -> pushItemsToContainer(container, pushDirection.getOpposite()))*/
					.orElse(false))/*)*/ {
				break;
			}
		}

		for (Direction pullDirection : pullDirections) {
			if (getItemHandler(level, pos, pullDirection).map(this::pullItems)
					/*.orElse(getWorldlyContainer(level, pos, pullDirection).map(container -> pullItemsFromContainer(container, pullDirection.getOpposite()))*/
					.orElse(false))/*)*/ {
				break;
			}
		}

		coolDownTime = level.getGameTime() + upgradeItem.getTransferSpeedTicks();
	}

	// TODO: Necessary?
	/*private boolean pushItemsToContainer(WorldlyContainer worldlyContainer, Direction face) {
		ITrackedContentsItemHandler fromHandler = storageWrapper.getInventoryForUpgradeProcessing();

		for (int slot = 0; slot < fromHandler.getSlotCount(); slot++) {
			ItemStack slotStack = fromHandler.getStackInSlot(slot);
			if (!slotStack.isEmpty() && outputFilterLogic.matchesFilter(slotStack)) {
				try (Transaction extractionSimulation = Transaction.openOuter())
				fromHandler.extractSlot()
				ItemStack extractedStack = StorageUtil.simulateExtract(fromHandler, ) fromHandler.extractItem(slot, Math.min(worldlyContainer.getMaxStackSize(), upgradeItem.getMaxTransferStackSize()), true);
				if (!extractedStack.isEmpty() && pushStackToContainer(worldlyContainer, face, extractedStack, fromHandler, slot)) {
					return true;
				}
			}
		}

		return false;
	}
	// TODO: Fix
	private boolean pushStackToContainer(WorldlyContainer worldlyContainer, Direction face, ItemStack extractedStack, ITrackedContentsItemHandler fromHandler, int slotToExtractFrom) {
		for (int containerSlot = 0; containerSlot < worldlyContainer.getContainerSize(); containerSlot++) {
			if (worldlyContainer.canPlaceItemThroughFace(containerSlot, extractedStack, face)) {
				ItemStack existingStack = worldlyContainer.getItem(containerSlot);
				if (existingStack.isEmpty()) {
					worldlyContainer.setItem(containerSlot, extractedStack);
					fromHandler.extractItem(slotToExtractFrom, extractedStack.getCount(), false);
					return true;
				} else if (ItemHandlerHelper.canItemStacksStack(existingStack, extractedStack)) {
					int maxStackSize = Math.min(worldlyContainer.getMaxStackSize(), existingStack.getMaxStackSize());
					int remainder = maxStackSize - existingStack.getCount();
					if (remainder > 0) {
						int countToExtract = Math.min(extractedStack.getCount(), remainder);
						existingStack.grow(countToExtract);
						worldlyContainer.setItem(containerSlot, existingStack);
						fromHandler.extractItem(slotToExtractFrom, countToExtract, false);
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean pullItemsFromContainer(WorldlyContainer worldlyContainer, Direction face) {
		ITrackedContentsItemHandler toHandler = storageWrapper.getInventoryForUpgradeProcessing();
		for (int containerSlot = 0; containerSlot < worldlyContainer.getContainerSize(); containerSlot++) {
			ItemStack existingStack = worldlyContainer.getItem(containerSlot);
			if (!existingStack.isEmpty() && worldlyContainer.canTakeItemThroughFace(containerSlot, existingStack, face) && inputFilterLogic.matchesFilter(existingStack)) {
				ItemVariant resource = ItemVariant.of(existingStack);
				long maxAmount = existingStack.getCount();
				try (Transaction nested = Transaction.openNested(null)) {
					maxAmount -= toHandler.insert(resource, maxAmount, nested);
					nested.commit();
				}

				if (maxAmount > 0) {
					worldlyContainer.setItem(containerSlot, resource.toStack((int) maxAmount));
					return true;
				}
			}
		}

		return false;
	}*/

	private void initDirections(Level level, BlockPos pos) {
		if (upgrade.hasTag()) {
			return;
		}
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() instanceof StorageBlockBase storageBlock) {
			Direction horizontalDirection = storageBlock.getHorizontalDirection(state);
			VerticalFacing verticalFacing = storageBlock.getVerticalFacing(state);
			initDirections(BlockSide.BOTTOM.toDirection(horizontalDirection, verticalFacing), BlockSide.TOP.toDirection(horizontalDirection, verticalFacing));
		}
	}

	/*private Optional<WorldlyContainer> getWorldlyContainer(Level level, BlockPos pos, Direction direction) {
		BlockPos offsetPos = pos.relative(direction);
		BlockState state = level.getBlockState(offsetPos);
		if (state.getBlock() instanceof WorldlyContainerHolder worldlyContainerHolder) {
			return Optional.of(worldlyContainerHolder.getContainer(state, level, offsetPos));
		}
		return Optional.empty();
	}*/

	private boolean pullItems(Storage<ItemVariant> fromHandler) {
		return moveItems(fromHandler, storageWrapper.getInventoryForUpgradeProcessing(), inputFilterLogic);
	}

	private boolean pushItems(Storage<ItemVariant> toHandler) {
		return moveItems(storageWrapper.getInventoryForUpgradeProcessing(), toHandler, outputFilterLogic);
	}

	private boolean moveItems(Storage<ItemVariant> fromHandler, Storage<ItemVariant> toHandler, FilterLogic filterLogic) {
		try (Transaction iteration = Transaction.openOuter()) {
			for (StorageView<ItemVariant> view : fromHandler.nonEmptyViews()) {
				ItemVariant resource = view.getResource();
				ItemStack slotStack = resource.toStack((int) view.getAmount());
				if (!slotStack.isEmpty() && filterLogic.matchesFilter(slotStack)) {
					long maxExtracted;
					try (Transaction extractionTestTransaction = iteration.openNested()) {
						maxExtracted = fromHandler.extract(resource, upgradeItem.getMaxTransferStackSize(), extractionTestTransaction);
						extractionTestTransaction.abort();
					}

					try (Transaction transferTransaction = iteration.openNested()) {
						long accepted = toHandler.insert(resource, maxExtracted, transferTransaction);
						if (fromHandler.extract(resource, accepted, transferTransaction) == accepted) {
							transferTransaction.commit();
							iteration.commit();
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private Optional<Storage<ItemVariant>> getItemHandler(Level level, BlockPos pos, Direction direction) {
		if (!handlerCache.containsKey(direction)) {
			handlerCache.put(direction, BlockApiCache.create(ItemStorage.SIDED, (ServerLevel) level, pos.relative(direction)));
		}

		return Optional.ofNullable(handlerCache.get(direction).find(direction.getOpposite()));
	}

	public ContentsFilterLogic getInputFilterLogic() {
		return inputFilterLogic;
	}

	public ContentsFilterLogic getOutputFilterLogic() {
		return outputFilterLogic;
	}

	public boolean isPullingFrom(Direction direction) {
		return pullDirections.contains(direction);
	}

	public boolean isPushingTo(Direction direction) {
		return pushDirections.contains(direction);
	}

	public void setPullingFrom(Direction direction, boolean shouldPull) {
		if (shouldPull) {
			pullDirections.add(direction);
		} else {
			pullDirections.remove(direction);
		}
		serializePullDirections();
	}

	public void setPushingTo(Direction direction, boolean isPushing) {
		if (isPushing) {
			pushDirections.add(direction);
		} else {
			pushDirections.remove(direction);
		}
		serializePushDirections();
	}

	private void serializePullDirections() {
		NBTHelper.putList(upgrade.getOrCreateTag(), "pullDirections", pullDirections, d -> StringTag.valueOf(d.getSerializedName()));
		save();
	}

	private void serializePushDirections() {
		NBTHelper.putList(upgrade.getOrCreateTag(), "pushDirections", pushDirections, d -> StringTag.valueOf(d.getSerializedName()));
		save();
	}

	public void deserialize() {
		pullDirections.clear();
		pushDirections.clear();
		if (upgrade.hasTag()) {
			pullDirections = NBTHelper.getCollection(upgrade.getOrCreateTag(), "pullDirections", Tag.TAG_STRING, t -> Optional.ofNullable(Direction.byName(t.getAsString())), HashSet::new).orElseGet(HashSet::new);
			pushDirections = NBTHelper.getCollection(upgrade.getOrCreateTag(), "pushDirections", Tag.TAG_STRING, t -> Optional.ofNullable(Direction.byName(t.getAsString())), HashSet::new).orElseGet(HashSet::new);
		}
	}

	public void initDirections(Direction pushDirection, Direction pullDirection) {
		if (!upgrade.hasTag()) {
			setPushingTo(pushDirection, true);
			setPullingFrom(pullDirection, true);
		}
	}
}
