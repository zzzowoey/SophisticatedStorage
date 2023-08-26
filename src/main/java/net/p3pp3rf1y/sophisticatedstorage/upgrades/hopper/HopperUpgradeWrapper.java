package net.p3pp3rf1y.sophisticatedstorage.upgrades.hopper;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
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

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

import static net.fabricmc.fabric.api.transfer.v1.item.ItemStorage.SIDED;

public class HopperUpgradeWrapper extends UpgradeWrapperBase<HopperUpgradeWrapper, HopperUpgradeItem>
		implements ITickableUpgrade {

	private Set<Direction> pullDirections = new LinkedHashSet<>();
	private Set<Direction> pushDirections = new LinkedHashSet<>();
	private final Map<Direction, BlockApiCache<Storage<ItemVariant>, Direction>> handlerCache = new EnumMap<>(Direction.class);

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

	private boolean pullItem(Storage<ItemVariant> fromHandler) {
		return moveItems(fromHandler, storageWrapper.getInventoryForUpgradeProcessing(), inputFilterLogic);
	}

	private boolean pushItems(Storage<ItemVariant> toHandler) {
		return moveItems(storageWrapper.getInventoryForUpgradeProcessing(), toHandler, outputFilterLogic);
	}

	private boolean moveItems(Storage<ItemVariant> fromHandler, Storage<ItemVariant> toHandler, FilterLogic filterLogic) {
		try (Transaction iteration = Transaction.openOuter()) {
			for (StorageView<ItemVariant> view : fromHandler.nonEmptyViews()) {
				ItemStack slotStack = view.getResource().toStack((int) view.getAmount());
				if (!slotStack.isEmpty() && filterLogic.matchesFilter(slotStack)) {
					ItemVariant resource = ItemVariant.of(slotStack);
					long maxExtracted;
					try (Transaction extractionTestTransaction = iteration.openNested()) {
						maxExtracted = view.extract(resource, upgradeItem.getMaxTransferStackSize(), extractionTestTransaction);
						extractionTestTransaction.abort();
					}

					try (Transaction transferTransaction = iteration.openNested()) {
						long accepted = toHandler.insert(resource, maxExtracted, transferTransaction);
						if (view.extract(resource, accepted, transferTransaction) == accepted) {
							transferTransaction.commit();
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
			handlerCache.put(direction, BlockApiCache.create(SIDED, (ServerLevel) level, pos.relative(direction)));
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
