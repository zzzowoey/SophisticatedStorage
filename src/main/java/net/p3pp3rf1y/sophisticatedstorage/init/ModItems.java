package net.p3pp3rf1y.sophisticatedstorage.init;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.UpgradeGuiManager;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerRegistry;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerType;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ContentsFilteredUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.battery.BatteryInventoryPart;
import net.p3pp3rf1y.sophisticatedcore.upgrades.battery.BatteryUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.battery.BatteryUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.battery.BatteryUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.compacting.CompactingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.compacting.CompactingUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.compacting.CompactingUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.compacting.CompactingUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.cooking.*;
import net.p3pp3rf1y.sophisticatedcore.upgrades.crafting.CraftingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.crafting.CraftingUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.crafting.CraftingUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.crafting.CraftingUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.feeding.FeedingUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.feeding.FeedingUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.feeding.FeedingUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.feeding.FeedingUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.filter.FilterUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.filter.FilterUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.filter.FilterUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.JukeboxUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.JukeboxUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.JukeboxUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.magnet.MagnetUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.magnet.MagnetUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.magnet.MagnetUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.magnet.MagnetUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pickup.PickupUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pickup.PickupUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pickup.PickupUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pump.PumpUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pump.PumpUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pump.PumpUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.pump.PumpUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.stack.StackUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.stonecutter.StonecutterUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.stonecutter.StonecutterUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.stonecutter.StonecutterUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.stonecutter.StonecutterUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.tank.TankInventoryPart;
import net.p3pp3rf1y.sophisticatedcore.upgrades.tank.TankUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.tank.TankUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.tank.TankUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.voiding.VoidUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.xppump.XpPumpUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.xppump.XpPumpUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.xppump.XpPumpUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.upgrades.xppump.XpPumpUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedstorage.Config;
import net.p3pp3rf1y.sophisticatedstorage.SophisticatedStorage;
import net.p3pp3rf1y.sophisticatedstorage.client.gui.StorageButtonDefinitions;
import net.p3pp3rf1y.sophisticatedstorage.item.StorageTierUpgradeItem;
import net.p3pp3rf1y.sophisticatedstorage.item.StorageToolItem;
import net.p3pp3rf1y.sophisticatedstorage.upgrades.compression.CompressionUpgradeItem;
import net.p3pp3rf1y.sophisticatedstorage.upgrades.hopper.HopperUpgradeContainer;
import net.p3pp3rf1y.sophisticatedstorage.upgrades.hopper.HopperUpgradeItem;
import net.p3pp3rf1y.sophisticatedstorage.upgrades.hopper.HopperUpgradeTab;
import net.p3pp3rf1y.sophisticatedstorage.upgrades.hopper.HopperUpgradeWrapper;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
	static List<Item> ITEMS = new ArrayList<>(); // Must be up here!

	private ModItems() {}

	public static final ResourceLocation STORAGE_UPGRADE_TAG_NAME = new ResourceLocation(SophisticatedStorage.ID, "upgrade");

	public static final TagKey<Item> STORAGE_UPGRADE_TAG = TagKey.create(Registries.ITEM, STORAGE_UPGRADE_TAG_NAME);

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// ITEMS
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static final PickupUpgradeItem PICKUP_UPGRADE = register("pickup_upgrade",
			new PickupUpgradeItem(Config.SERVER.pickupUpgrade.filterSlots::get));
	public static final PickupUpgradeItem ADVANCED_PICKUP_UPGRADE = register("advanced_pickup_upgrade",
			new PickupUpgradeItem(Config.SERVER.advancedPickupUpgrade.filterSlots::get));
	public static final FilterUpgradeItem FILTER_UPGRADE = register("filter_upgrade",
			new FilterUpgradeItem(Config.SERVER.filterUpgrade.filterSlots::get));
	public static final FilterUpgradeItem ADVANCED_FILTER_UPGRADE = register("advanced_filter_upgrade",
			new FilterUpgradeItem(Config.SERVER.advancedFilterUpgrade.filterSlots::get));
	public static final MagnetUpgradeItem MAGNET_UPGRADE = register("magnet_upgrade",
			new MagnetUpgradeItem(Config.SERVER.magnetUpgrade.magnetRange::get, Config.SERVER.magnetUpgrade.filterSlots::get));
	public static final MagnetUpgradeItem ADVANCED_MAGNET_UPGRADE = register("advanced_magnet_upgrade",
			new MagnetUpgradeItem(Config.SERVER.advancedMagnetUpgrade.magnetRange::get, Config.SERVER.advancedMagnetUpgrade.filterSlots::get));
	public static final FeedingUpgradeItem FEEDING_UPGRADE = register("feeding_upgrade",
			new FeedingUpgradeItem(Config.SERVER.feedingUpgrade.filterSlots::get));
	public static final FeedingUpgradeItem ADVANCED_FEEDING_UPGRADE = register("advanced_feeding_upgrade",
			new FeedingUpgradeItem(Config.SERVER.advancedFeedingUpgrade.filterSlots::get));
	public static final CompactingUpgradeItem COMPACTING_UPGRADE = register("compacting_upgrade",
			new CompactingUpgradeItem(false, Config.SERVER.compactingUpgrade.filterSlots::get));
	public static final CompactingUpgradeItem ADVANCED_COMPACTING_UPGRADE = register("advanced_compacting_upgrade",
			new CompactingUpgradeItem(true, Config.SERVER.advancedCompactingUpgrade.filterSlots::get));
	public static final VoidUpgradeItem VOID_UPGRADE = register("void_upgrade",
			new VoidUpgradeItem(Config.SERVER.voidUpgrade));
	public static final VoidUpgradeItem ADVANCED_VOID_UPGRADE = register("advanced_void_upgrade",
			new VoidUpgradeItem(Config.SERVER.advancedVoidUpgrade));
	public static final SmeltingUpgradeItem SMELTING_UPGRADE = register("smelting_upgrade",
			new SmeltingUpgradeItem(Config.SERVER.smeltingUpgrade));
	public static final AutoSmeltingUpgradeItem AUTO_SMELTING_UPGRADE = register("auto_smelting_upgrade",
			new AutoSmeltingUpgradeItem(Config.SERVER.autoSmeltingUpgrade));
	public static final SmokingUpgradeItem SMOKING_UPGRADE = register("smoking_upgrade",
			new SmokingUpgradeItem(Config.SERVER.smokingUpgrade));
	public static final AutoSmokingUpgradeItem AUTO_SMOKING_UPGRADE = register("auto_smoking_upgrade",
			new AutoSmokingUpgradeItem(Config.SERVER.autoSmokingUpgrade));
	public static final BlastingUpgradeItem BLASTING_UPGRADE = register("blasting_upgrade",
			new BlastingUpgradeItem(Config.SERVER.blastingUpgrade));
	public static final AutoBlastingUpgradeItem AUTO_BLASTING_UPGRADE = register("auto_blasting_upgrade",
			new AutoBlastingUpgradeItem(Config.SERVER.autoBlastingUpgrade));
	public static final CraftingUpgradeItem CRAFTING_UPGRADE = register("crafting_upgrade",
			new CraftingUpgradeItem());
	public static final StonecutterUpgradeItem STONECUTTER_UPGRADE = register("stonecutter_upgrade",
			new StonecutterUpgradeItem());
	public static final StackUpgradeItem STACK_UPGRADE_TIER_1 = register("stack_upgrade_tier_1",
			new StackUpgradeItem(2));
	public static final StackUpgradeItem STACK_UPGRADE_TIER_2 = register("stack_upgrade_tier_2",
			new StackUpgradeItem(4));
	public static final StackUpgradeItem STACK_UPGRADE_TIER_3 = register("stack_upgrade_tier_3",
			new StackUpgradeItem(8));
	public static final StackUpgradeItem STACK_UPGRADE_TIER_4 = register("stack_upgrade_tier_4",
			new StackUpgradeItem(16));
	public static final JukeboxUpgradeItem JUKEBOX_UPGRADE = register("jukebox_upgrade", new JukeboxUpgradeItem());
	public static final PumpUpgradeItem PUMP_UPGRADE = register("pump_upgrade", new PumpUpgradeItem(false, false, Config.SERVER.pumpUpgrade));
	public static final PumpUpgradeItem ADVANCED_PUMP_UPGRADE = register("advanced_pump_upgrade", new PumpUpgradeItem(true, true, Config.SERVER.pumpUpgrade));
	public static final XpPumpUpgradeItem XP_PUMP_UPGRADE = register("xp_pump_upgrade", new XpPumpUpgradeItem(Config.SERVER.xpPumpUpgrade));
	public static final CompressionUpgradeItem COMPRESSION_UPGRADE = register("compression_upgrade", new CompressionUpgradeItem());
	public static final HopperUpgradeItem HOPPER_UPGRADE = register("hopper_upgrade", new HopperUpgradeItem(
			Config.SERVER.hopperUpgrade.inputFilterSlots::get, Config.SERVER.hopperUpgrade.outputFilterSlots::get, Config.SERVER.hopperUpgrade.transferSpeedTicks::get, Config.SERVER.hopperUpgrade.maxTransferStackSize::get));
	public static final HopperUpgradeItem ADVANCED_HOPPER_UPGRADE = register("advanced_hopper_upgrade", new HopperUpgradeItem(
			Config.SERVER.advancedHopperUpgrade.inputFilterSlots::get, Config.SERVER.advancedHopperUpgrade.outputFilterSlots::get, Config.SERVER.advancedHopperUpgrade.transferSpeedTicks::get, Config.SERVER.advancedHopperUpgrade.maxTransferStackSize::get));
	public static final StorageTierUpgradeItem BASIC_TIER_UPGRADE = register("basic_tier_upgrade", new StorageTierUpgradeItem(StorageTierUpgradeItem.TierUpgrade.BASIC, true));
	public static final StorageTierUpgradeItem BASIC_TO_IRON_TIER_UPGRADE = register("basic_to_iron_tier_upgrade", new StorageTierUpgradeItem(StorageTierUpgradeItem.TierUpgrade.BASIC_TO_IRON));
	public static final StorageTierUpgradeItem BASIC_TO_GOLD_TIER_UPGRADE = register("basic_to_gold_tier_upgrade", new StorageTierUpgradeItem(StorageTierUpgradeItem.TierUpgrade.BASIC_TO_GOLD));
	public static final StorageTierUpgradeItem BASIC_TO_DIAMOND_TIER_UPGRADE = register("basic_to_diamond_tier_upgrade", new StorageTierUpgradeItem(StorageTierUpgradeItem.TierUpgrade.BASIC_TO_DIAMOND));
	public static final StorageTierUpgradeItem BASIC_TO_NETHERITE_TIER_UPGRADE = register("basic_to_netherite_tier_upgrade", new StorageTierUpgradeItem(StorageTierUpgradeItem.TierUpgrade.BASIC_TO_NETHERITE));
	public static final StorageTierUpgradeItem IRON_TO_GOLD_TIER_UPGRADE = register("iron_to_gold_tier_upgrade", new StorageTierUpgradeItem(StorageTierUpgradeItem.TierUpgrade.IRON_TO_GOLD));
	public static final StorageTierUpgradeItem IRON_TO_DIAMOND_TIER_UPGRADE = register("iron_to_diamond_tier_upgrade", new StorageTierUpgradeItem(StorageTierUpgradeItem.TierUpgrade.IRON_TO_DIAMOND));
	public static final StorageTierUpgradeItem IRON_TO_NETHERITE_TIER_UPGRADE = register("iron_to_netherite_tier_upgrade", new StorageTierUpgradeItem(StorageTierUpgradeItem.TierUpgrade.IRON_TO_NETHERITE));
	public static final StorageTierUpgradeItem GOLD_TO_DIAMOND_TIER_UPGRADE = register("gold_to_diamond_tier_upgrade", new StorageTierUpgradeItem(StorageTierUpgradeItem.TierUpgrade.GOLD_TO_DIAMOND));
	public static final StorageTierUpgradeItem GOLD_TO_NETHERITE_TIER_UPGRADE = register("gold_to_netherite_tier_upgrade", new StorageTierUpgradeItem(StorageTierUpgradeItem.TierUpgrade.GOLD_TO_NETHERITE));
	public static final StorageTierUpgradeItem DIAMOND_TO_NETHERITE_TIER_UPGRADE = register("diamond_to_netherite_tier_upgrade", new StorageTierUpgradeItem(StorageTierUpgradeItem.TierUpgrade.DIAMOND_TO_NETHERITE));

	public static final Item UPGRADE_BASE = register("upgrade_base", new Item(new Item.Properties().stacksTo(16)));

	public static final Item PACKING_TAPE = register("packing_tape", new Item(new Item.Properties().stacksTo(1).durability(4)));
	public static final Item STORAGE_TOOL = register("storage_tool", new StorageToolItem());
	public static final Item DEBUG_TOOL = register("debug_tool", new Item(new Item.Properties().stacksTo(1)));
	public static final Item INACCESSIBLE_SLOT = register("inaccessible_slot", new Item(new Item.Properties().stacksTo(1)));
	
	
	public static <T extends Item> T register(String id, T value) {
		ITEMS.add(value);
		return Registry.register(BuiltInRegistries.ITEM, SophisticatedStorage.getRL(id), value);
	}
	
	private static void registerItemGroup() {
		ItemGroupEvents.modifyEntriesEvent(SophisticatedStorage.CREATIVE_TAB).register(entries -> {
			ITEMS.stream().filter(item -> item != INACCESSIBLE_SLOT).forEach(entries::accept);
		});
	}
	
	public static void register() {
		registerContainers();
		registerItemGroup();
	}

	private static final UpgradeContainerType<PickupUpgradeWrapper, ContentsFilteredUpgradeContainer<PickupUpgradeWrapper>> PICKUP_BASIC_TYPE = new UpgradeContainerType<>(ContentsFilteredUpgradeContainer::new);
	private static final UpgradeContainerType<PickupUpgradeWrapper, ContentsFilteredUpgradeContainer<PickupUpgradeWrapper>> PICKUP_ADVANCED_TYPE = new UpgradeContainerType<>(ContentsFilteredUpgradeContainer::new);
	private static final UpgradeContainerType<MagnetUpgradeWrapper, MagnetUpgradeContainer> MAGNET_BASIC_TYPE = new UpgradeContainerType<>(MagnetUpgradeContainer::new);
	private static final UpgradeContainerType<MagnetUpgradeWrapper, MagnetUpgradeContainer> MAGNET_ADVANCED_TYPE = new UpgradeContainerType<>(MagnetUpgradeContainer::new);
	private static final UpgradeContainerType<FeedingUpgradeWrapper, FeedingUpgradeContainer> FEEDING_TYPE = new UpgradeContainerType<>(FeedingUpgradeContainer::new);
	private static final UpgradeContainerType<FeedingUpgradeWrapper, FeedingUpgradeContainer> ADVANCED_FEEDING_TYPE = new UpgradeContainerType<>(FeedingUpgradeContainer::new);
	private static final UpgradeContainerType<CompactingUpgradeWrapper, CompactingUpgradeContainer> COMPACTING_TYPE = new UpgradeContainerType<>(CompactingUpgradeContainer::new);
	private static final UpgradeContainerType<CompactingUpgradeWrapper, CompactingUpgradeContainer> ADVANCED_COMPACTING_TYPE = new UpgradeContainerType<>(CompactingUpgradeContainer::new);
	private static final UpgradeContainerType<VoidUpgradeWrapper, VoidUpgradeContainer> VOID_TYPE = new UpgradeContainerType<>(VoidUpgradeContainer::new);
	private static final UpgradeContainerType<VoidUpgradeWrapper, VoidUpgradeContainer> ADVANCED_VOID_TYPE = new UpgradeContainerType<>(VoidUpgradeContainer::new);
	private static final UpgradeContainerType<CookingUpgradeWrapper.SmeltingUpgradeWrapper, CookingUpgradeContainer<SmeltingRecipe, CookingUpgradeWrapper.SmeltingUpgradeWrapper>> SMELTING_TYPE = new UpgradeContainerType<>(CookingUpgradeContainer::new);
	private static final UpgradeContainerType<AutoCookingUpgradeWrapper.AutoSmeltingUpgradeWrapper, AutoCookingUpgradeContainer<SmeltingRecipe, AutoCookingUpgradeWrapper.AutoSmeltingUpgradeWrapper>> AUTO_SMELTING_TYPE = new UpgradeContainerType<>(AutoCookingUpgradeContainer::new);
	private static final UpgradeContainerType<CookingUpgradeWrapper.SmokingUpgradeWrapper, CookingUpgradeContainer<SmokingRecipe, CookingUpgradeWrapper.SmokingUpgradeWrapper>> SMOKING_TYPE = new UpgradeContainerType<>(CookingUpgradeContainer::new);
	private static final UpgradeContainerType<AutoCookingUpgradeWrapper.AutoSmokingUpgradeWrapper, AutoCookingUpgradeContainer<SmokingRecipe, AutoCookingUpgradeWrapper.AutoSmokingUpgradeWrapper>> AUTO_SMOKING_TYPE = new UpgradeContainerType<>(AutoCookingUpgradeContainer::new);
	private static final UpgradeContainerType<CookingUpgradeWrapper.BlastingUpgradeWrapper, CookingUpgradeContainer<BlastingRecipe, CookingUpgradeWrapper.BlastingUpgradeWrapper>> BLASTING_TYPE = new UpgradeContainerType<>(CookingUpgradeContainer::new);
	private static final UpgradeContainerType<AutoCookingUpgradeWrapper.AutoBlastingUpgradeWrapper, AutoCookingUpgradeContainer<BlastingRecipe, AutoCookingUpgradeWrapper.AutoBlastingUpgradeWrapper>> AUTO_BLASTING_TYPE = new UpgradeContainerType<>(AutoCookingUpgradeContainer::new);
	private static final UpgradeContainerType<CraftingUpgradeWrapper, CraftingUpgradeContainer> CRAFTING_TYPE = new UpgradeContainerType<>(CraftingUpgradeContainer::new);
	private static final UpgradeContainerType<StonecutterUpgradeWrapper, StonecutterUpgradeContainer> STONECUTTER_TYPE = new UpgradeContainerType<>(StonecutterUpgradeContainer::new);
	private static final UpgradeContainerType<JukeboxUpgradeItem.Wrapper, JukeboxUpgradeContainer> JUKEBOX_TYPE = new UpgradeContainerType<>(JukeboxUpgradeContainer::new);
	private static final UpgradeContainerType<TankUpgradeWrapper, TankUpgradeContainer> TANK_TYPE = new UpgradeContainerType<>(TankUpgradeContainer::new);
	private static final UpgradeContainerType<BatteryUpgradeWrapper, BatteryUpgradeContainer> BATTERY_TYPE = new UpgradeContainerType<>(BatteryUpgradeContainer::new);
	private static final UpgradeContainerType<PumpUpgradeWrapper, PumpUpgradeContainer> PUMP_TYPE = new UpgradeContainerType<>(PumpUpgradeContainer::new);
	private static final UpgradeContainerType<PumpUpgradeWrapper, PumpUpgradeContainer> ADVANCED_PUMP_TYPE = new UpgradeContainerType<>(PumpUpgradeContainer::new);
	private static final UpgradeContainerType<XpPumpUpgradeWrapper, XpPumpUpgradeContainer> XP_PUMP_TYPE = new UpgradeContainerType<>(XpPumpUpgradeContainer::new);
	private static final UpgradeContainerType<HopperUpgradeWrapper, HopperUpgradeContainer> HOPPER_TYPE = new UpgradeContainerType<>(HopperUpgradeContainer::new);
	private static final UpgradeContainerType<HopperUpgradeWrapper, HopperUpgradeContainer> ADVANCED_HOPPER_TYPE = new UpgradeContainerType<>(HopperUpgradeContainer::new);

	public static void registerContainers() {
		UpgradeContainerRegistry.register(PICKUP_UPGRADE, PICKUP_BASIC_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_PICKUP_UPGRADE, PICKUP_ADVANCED_TYPE);
		UpgradeContainerRegistry.register(FILTER_UPGRADE, FilterUpgradeContainer.BASIC_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_FILTER_UPGRADE, FilterUpgradeContainer.ADVANCED_TYPE);
		UpgradeContainerRegistry.register(MAGNET_UPGRADE, MAGNET_BASIC_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_MAGNET_UPGRADE, MAGNET_ADVANCED_TYPE);
		UpgradeContainerRegistry.register(FEEDING_UPGRADE, FEEDING_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_FEEDING_UPGRADE, ADVANCED_FEEDING_TYPE);
		UpgradeContainerRegistry.register(COMPACTING_UPGRADE, COMPACTING_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_COMPACTING_UPGRADE, ADVANCED_COMPACTING_TYPE);
		UpgradeContainerRegistry.register(VOID_UPGRADE, VOID_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_VOID_UPGRADE, ADVANCED_VOID_TYPE);
		UpgradeContainerRegistry.register(SMELTING_UPGRADE, SMELTING_TYPE);
		UpgradeContainerRegistry.register(AUTO_SMELTING_UPGRADE, AUTO_SMELTING_TYPE);
		UpgradeContainerRegistry.register(SMOKING_UPGRADE, SMOKING_TYPE);
		UpgradeContainerRegistry.register(AUTO_SMOKING_UPGRADE, AUTO_SMOKING_TYPE);
		UpgradeContainerRegistry.register(BLASTING_UPGRADE, BLASTING_TYPE);
		UpgradeContainerRegistry.register(AUTO_BLASTING_UPGRADE, AUTO_BLASTING_TYPE);
		UpgradeContainerRegistry.register(CRAFTING_UPGRADE, CRAFTING_TYPE);
		UpgradeContainerRegistry.register(STONECUTTER_UPGRADE, STONECUTTER_TYPE);
		UpgradeContainerRegistry.register(JUKEBOX_UPGRADE, JUKEBOX_TYPE);
		UpgradeContainerRegistry.register(PUMP_UPGRADE, PUMP_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_PUMP_UPGRADE, ADVANCED_PUMP_TYPE);
		UpgradeContainerRegistry.register(XP_PUMP_UPGRADE, XP_PUMP_TYPE);
		UpgradeContainerRegistry.register(HOPPER_UPGRADE, HOPPER_TYPE);
		UpgradeContainerRegistry.register(ADVANCED_HOPPER_UPGRADE, ADVANCED_HOPPER_TYPE);

		EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> {
			UpgradeGuiManager.registerTab(FEEDING_TYPE, (FeedingUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
					new FeedingUpgradeTab.Basic(uc, p, s, Config.SERVER.feedingUpgrade.slotsInRow.get()));
			UpgradeGuiManager.registerTab(ADVANCED_FEEDING_TYPE, (FeedingUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
					new FeedingUpgradeTab.Advanced(uc, p, s, Config.SERVER.advancedFeedingUpgrade.slotsInRow.get()));
			UpgradeGuiManager.registerTab(PICKUP_BASIC_TYPE, (ContentsFilteredUpgradeContainer<PickupUpgradeWrapper> uc, Position p, StorageScreenBase<?> s) ->
					new PickupUpgradeTab.Basic(uc, p, s, Config.SERVER.pickupUpgrade.slotsInRow.get(), StorageButtonDefinitions.STORAGE_CONTENTS_FILTER_TYPE));
			UpgradeGuiManager.registerTab(PICKUP_ADVANCED_TYPE, (ContentsFilteredUpgradeContainer<PickupUpgradeWrapper> uc, Position p, StorageScreenBase<?> s) ->
					new PickupUpgradeTab.Advanced(uc, p, s, Config.SERVER.advancedPickupUpgrade.slotsInRow.get(), StorageButtonDefinitions.STORAGE_CONTENTS_FILTER_TYPE));
			UpgradeGuiManager.registerTab(FilterUpgradeContainer.BASIC_TYPE, (FilterUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
					new FilterUpgradeTab.Basic(uc, p, s, Config.SERVER.filterUpgrade.slotsInRow.get(), StorageButtonDefinitions.STORAGE_CONTENTS_FILTER_TYPE));
			UpgradeGuiManager.registerTab(FilterUpgradeContainer.ADVANCED_TYPE, (FilterUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
					new FilterUpgradeTab.Advanced(uc, p, s, Config.SERVER.advancedFilterUpgrade.slotsInRow.get(), StorageButtonDefinitions.STORAGE_CONTENTS_FILTER_TYPE));
			UpgradeGuiManager.registerTab(MAGNET_BASIC_TYPE, (MagnetUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
					new MagnetUpgradeTab.Basic(uc, p, s, Config.SERVER.magnetUpgrade.slotsInRow.get(), StorageButtonDefinitions.STORAGE_CONTENTS_FILTER_TYPE));
			UpgradeGuiManager.registerTab(MAGNET_ADVANCED_TYPE, (MagnetUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
					new MagnetUpgradeTab.Advanced(uc, p, s, Config.SERVER.advancedMagnetUpgrade.slotsInRow.get(), StorageButtonDefinitions.STORAGE_CONTENTS_FILTER_TYPE));
			UpgradeGuiManager.registerTab(COMPACTING_TYPE, (CompactingUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
					new CompactingUpgradeTab.Basic(uc, p, s, Config.SERVER.compactingUpgrade.slotsInRow.get()));
			UpgradeGuiManager.registerTab(ADVANCED_COMPACTING_TYPE, (CompactingUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
					new CompactingUpgradeTab.Advanced(uc, p, s, Config.SERVER.advancedCompactingUpgrade.slotsInRow.get()));
			UpgradeGuiManager.registerTab(VOID_TYPE, (VoidUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
					new VoidUpgradeTab.Basic(uc, p, s, Config.SERVER.voidUpgrade.slotsInRow.get()));
			UpgradeGuiManager.registerTab(ADVANCED_VOID_TYPE, (VoidUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
					new VoidUpgradeTab.Advanced(uc, p, s, Config.SERVER.advancedVoidUpgrade.slotsInRow.get()));
			UpgradeGuiManager.registerTab(SMELTING_TYPE, CookingUpgradeTab.SmeltingUpgradeTab::new);
			UpgradeGuiManager.registerTab(AUTO_SMELTING_TYPE, (AutoCookingUpgradeContainer<SmeltingRecipe, AutoCookingUpgradeWrapper.AutoSmeltingUpgradeWrapper> uc, Position p, StorageScreenBase<?> s) ->
					new AutoCookingUpgradeTab.AutoSmeltingUpgradeTab(uc, p, s, Config.SERVER.autoSmeltingUpgrade.inputFilterSlotsInRow.get(), Config.SERVER.autoSmeltingUpgrade.fuelFilterSlotsInRow.get()));
			UpgradeGuiManager.registerTab(SMOKING_TYPE, CookingUpgradeTab.SmokingUpgradeTab::new);
			UpgradeGuiManager.registerTab(AUTO_SMOKING_TYPE, (AutoCookingUpgradeContainer<SmokingRecipe, AutoCookingUpgradeWrapper.AutoSmokingUpgradeWrapper> uc, Position p, StorageScreenBase<?> s) ->
					new AutoCookingUpgradeTab.AutoSmokingUpgradeTab(uc, p, s, Config.SERVER.autoSmokingUpgrade.inputFilterSlotsInRow.get(), Config.SERVER.autoSmokingUpgrade.fuelFilterSlotsInRow.get()));
			UpgradeGuiManager.registerTab(BLASTING_TYPE, CookingUpgradeTab.BlastingUpgradeTab::new);
			UpgradeGuiManager.registerTab(AUTO_BLASTING_TYPE, (AutoCookingUpgradeContainer<BlastingRecipe, AutoCookingUpgradeWrapper.AutoBlastingUpgradeWrapper> uc, Position p, StorageScreenBase<?> s) ->
					new AutoCookingUpgradeTab.AutoBlastingUpgradeTab(uc, p, s, Config.SERVER.autoBlastingUpgrade.inputFilterSlotsInRow.get(), Config.SERVER.autoBlastingUpgrade.fuelFilterSlotsInRow.get()));
			UpgradeGuiManager.registerTab(CRAFTING_TYPE, (CraftingUpgradeContainer uc, Position p, StorageScreenBase<?> s) ->
					new CraftingUpgradeTab(uc, p, s, StorageButtonDefinitions.SHIFT_CLICK_TARGET));
			UpgradeGuiManager.registerTab(STONECUTTER_TYPE, (StonecutterUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen) ->
					new StonecutterUpgradeTab(upgradeContainer, position, screen, StorageButtonDefinitions.SHIFT_CLICK_TARGET));
			UpgradeGuiManager.registerTab(JUKEBOX_TYPE, JukeboxUpgradeTab::new);
			UpgradeGuiManager.registerTab(TANK_TYPE, TankUpgradeTab::new);
			UpgradeGuiManager.registerTab(BATTERY_TYPE, BatteryUpgradeTab::new);
			UpgradeGuiManager.registerInventoryPart(TANK_TYPE, TankInventoryPart::new);
			UpgradeGuiManager.registerInventoryPart(BATTERY_TYPE, BatteryInventoryPart::new);
			UpgradeGuiManager.registerTab(PUMP_TYPE, PumpUpgradeTab.Basic::new);
			UpgradeGuiManager.registerTab(ADVANCED_PUMP_TYPE, PumpUpgradeTab.Advanced::new);
			UpgradeGuiManager.registerTab(XP_PUMP_TYPE, (XpPumpUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen) ->
					new XpPumpUpgradeTab(upgradeContainer, position, screen, Config.SERVER.xpPumpUpgrade.mendingOn.get()));
			UpgradeGuiManager.registerTab(HOPPER_TYPE, HopperUpgradeTab.Basic::new);
			UpgradeGuiManager.registerTab(ADVANCED_HOPPER_TYPE, HopperUpgradeTab.Advanced::new);
		});
	}
}
