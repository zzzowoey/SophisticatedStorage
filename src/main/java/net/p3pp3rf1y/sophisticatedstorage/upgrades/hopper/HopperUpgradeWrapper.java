package net.p3pp3rf1y.sophisticatedstorage.upgrades.hopper;

import io.github.fabricators_of_create.porting_lib.transfer.item.SlottedStackStorage;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
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
import net.p3pp3rf1y.sophisticatedcore.common.lookup.ItemStorage;
import net.p3pp3rf1y.sophisticatedcore.settings.memory.MemorySettingsCategory;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilterLogic;
import net.p3pp3rf1y.sophisticatedcore.upgrades.FilterLogic;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ITickableUpgrade;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeWrapperBase;
import net.p3pp3rf1y.sophisticatedcore.util.NBTHelper;
import net.p3pp3rf1y.sophisticatedstorage.block.StorageBlockBase;
import net.p3pp3rf1y.sophisticatedstorage.block.VerticalFacing;
import net.p3pp3rf1y.sophisticatedstorage.common.gui.BlockSide;
import net.p3pp3rf1y.sophisticatedstorage.upgrades.INeighborChangeListenerUpgrade;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class HopperUpgradeWrapper extends UpgradeWrapperBase<HopperUpgradeWrapper, HopperUpgradeItem>
		implements ITickableUpgrade, INeighborChangeListenerUpgrade {

	private Set<Direction> pullDirections = new LinkedHashSet<>();
	private Set<Direction> pushDirections = new LinkedHashSet<>();
	private BlockApiCache<SlottedStackStorage, Direction> handlerCache;

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
			if (getItemHandler(level, pos, pushDirection).map(this::pushItems).orElse(false)) {
				break;
			}
		}
		for (Direction pullDirection : pullDirections) {
			if (getItemHandler(level, pos, pullDirection).map(this::pullItem).orElse(false)) {
				break;
			}
		}

		coolDownTime = level.getGameTime() + upgradeItem.getTransferSpeedTicks();
	}

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

	private boolean pullItem(SlottedStackStorage fromHandler) {
		return moveItems(fromHandler, storageWrapper.getInventoryForUpgradeProcessing(), inputFilterLogic);
	}

	private boolean pushItems(SlottedStackStorage toHandler) {
		return moveItems(storageWrapper.getInventoryForUpgradeProcessing(), toHandler, outputFilterLogic);
	}

	private boolean moveItems(SlottedStackStorage fromHandler, SlottedStackStorage toHandler, FilterLogic filterLogic) {
		for (int slot = 0; slot < fromHandler.getSlotCount(); slot++) {
			ItemStack slotStack = fromHandler.getStackInSlot(slot);
			if (!slotStack.isEmpty() && filterLogic.matchesFilter(slotStack)) {
				ItemVariant resource = ItemVariant.of(slotStack);
				long extracted;
				try (Transaction simulate = Transaction.openOuter()) {
					extracted = fromHandler.extractSlot(slot, resource, upgradeItem.getMaxTransferStackSize(), simulate);
				}
				if (extracted > 0) {
					try (Transaction ctx = Transaction.openOuter()) {
						long inserted = toHandler.insert(resource, extracted, ctx);
						if (inserted < extracted) {
							fromHandler.extractSlot(slot, resource, inserted, ctx);
							ctx.commit();
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public void onNeighborChange(Level level, BlockPos pos, Direction direction) {
		// TODO: I don't think this is still necessary.
		/*if (pushDirections.contains(direction) || pullDirections.contains(direction)) {
			updateCacheOnSide(level, pos, direction);
		}*/
	}

	private Optional<SlottedStackStorage> getItemHandler(Level level, BlockPos pos, Direction direction) {
		if (handlerCache == null) {
			handlerCache = BlockApiCache.create(ItemStorage.SIDED, (ServerLevel) level, pos);
		}

		return Optional.ofNullable(handlerCache.find(direction));
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
