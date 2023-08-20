package net.p3pp3rf1y.sophisticatedstorage.client.render;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import io.github.fabricators_of_create.porting_lib.models.QuadTransformers;
import io.github.fabricators_of_create.porting_lib.models.util.TransformationHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.fabricmc.fabric.impl.client.indigo.renderer.mesh.MeshBuilderImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.p3pp3rf1y.sophisticatedcore.inventory.ItemStackKey;
import net.p3pp3rf1y.sophisticatedcore.renderdata.RenderInfo;
import net.p3pp3rf1y.sophisticatedstorage.block.BarrelBlock;
import net.p3pp3rf1y.sophisticatedstorage.block.BarrelBlockEntity;
import net.p3pp3rf1y.sophisticatedstorage.block.BarrelMaterial;
import net.p3pp3rf1y.sophisticatedstorage.block.VerticalFacing;
import net.p3pp3rf1y.sophisticatedstorage.client.util.LazyQuadTransformer;
import net.p3pp3rf1y.sophisticatedstorage.client.util.QuaternionHelper;
import net.p3pp3rf1y.sophisticatedstorage.common.gui.BlockSide;
import net.p3pp3rf1y.sophisticatedstorage.init.ModItems;
import net.p3pp3rf1y.sophisticatedstorage.item.BarrelBlockItem;
import net.p3pp3rf1y.sophisticatedstorage.item.StorageBlockItem;
import net.p3pp3rf1y.sophisticatedstorage.item.WoodStorageBlockItem;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.fabricmc.fabric.api.renderer.v1.mesh.QuadView.VANILLA_QUAD_STRIDE;
import static net.p3pp3rf1y.sophisticatedstorage.client.render.DisplayItemRenderer.*;

public abstract class BarrelBakedModelBase implements BakedModel, FabricBakedModel, IDataModel {
	private static final RenderContext.QuadTransform MOVE_TO_CORNER = QuadTransformers.applying(new Transformation(new Vector3f(-.5f, -.5f, -.5f), null, null, null));
	public static final Map<Direction, RenderContext.QuadTransform> DIRECTION_ROTATES = Map.of(
			Direction.UP, getDirectionRotationTransform(Direction.UP),
			Direction.DOWN, getDirectionRotationTransform(Direction.DOWN),
			Direction.NORTH, getDirectionRotationTransform(Direction.NORTH),
			Direction.SOUTH, getDirectionRotationTransform(Direction.SOUTH),
			Direction.WEST, getDirectionRotationTransform(Direction.WEST),
			Direction.EAST, getDirectionRotationTransform(Direction.EAST)
	);
	private static final LoadingCache<Direction, Cache<Integer, RenderContext.QuadTransform>> DIRECTION_MOVES_3D_ITEMS = CacheBuilder.newBuilder().expireAfterAccess(10L, TimeUnit.MINUTES).build(new CacheLoader<>() {
		@Override
		public Cache<Integer, RenderContext.QuadTransform> load(Direction key) {
			return CacheBuilder.newBuilder().expireAfterAccess(10L, TimeUnit.MINUTES).build();
		}
	});
	private static final RenderContext.QuadTransform SCALE_BIG_2D_ITEM = QuadTransformers.applying(new Transformation(null, null, new Vector3f(BIG_2D_ITEM_SCALE, BIG_2D_ITEM_SCALE, BIG_2D_ITEM_SCALE), null));
	private static final RenderContext.QuadTransform SCALE_SMALL_3D_ITEM = QuadTransformers.applying(new Transformation(null, null, new Vector3f(SMALL_3D_ITEM_SCALE, SMALL_3D_ITEM_SCALE, SMALL_3D_ITEM_SCALE), null));
	private static final RenderContext.QuadTransform SCALE_SMALL_2D_ITEM = QuadTransformers.applying(new Transformation(null, null, new Vector3f(SMALL_2D_ITEM_SCALE, SMALL_2D_ITEM_SCALE, SMALL_2D_ITEM_SCALE), null));
	private static final Cache<Integer, RenderContext.QuadTransform> DIRECTION_MOVE_BACK_TO_SIDE = CacheBuilder.newBuilder().expireAfterAccess(10L, TimeUnit.MINUTES).build();
	public static final Cache<Integer, List<BakedQuad>> BAKED_QUADS_CACHE = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.MINUTES).build();
	private static final Map<Integer, RenderContext.QuadTransform> DISPLAY_ROTATIONS = new HashMap<>();
	private static final ItemTransforms ITEM_TRANSFORMS = createItemTransforms();
	private static final List<BarrelMaterial> PARTICLE_ICON_MATERIAL_PRIORITY = List.of(BarrelMaterial.ALL, BarrelMaterial.ALL_BUT_TRIM, BarrelMaterial.TOP_ALL, BarrelMaterial.TOP);
	@SuppressWarnings("java:S4738") //ItemTransforms require Guava ImmutableMap to be passed in so no way to change that to java Map
	private static ItemTransforms createItemTransforms() {
		return new ItemTransforms(
				new ItemTransform(new Vector3f(75, 45, 0), new Vector3f(0, 2.5f / 16f, 0), new Vector3f(0.375f, 0.375f, 0.375f)),
				new ItemTransform(new Vector3f(75, 45, 0), new Vector3f(0, 2.5f / 16f, 0), new Vector3f(0.375f, 0.375f, 0.375f)),
				new ItemTransform(new Vector3f(0, 225, 0), new Vector3f(0, 0, 0), new Vector3f(0.4f, 0.4f, 0.4f)),
				new ItemTransform(new Vector3f(0, 45, 0), new Vector3f(0, 0, 0), new Vector3f(0.4f, 0.4f, 0.4f)),
				new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 14.25f / 16f, 0), new Vector3f(1, 1, 1)),
				new ItemTransform(new Vector3f(30, 45, 0), new Vector3f(0, 0, 0), new Vector3f(0.625f, 0.625f, 0.625f)),
				new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 3 / 16f, 0), new Vector3f(0.25f, 0.25f, 0.25f)),
				new ItemTransform(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0.5f, 0.5f, 0.5f))
		);
	}

	public static void invalidateCache() {
		DIRECTION_MOVES_3D_ITEMS.invalidateAll();
		DIRECTION_MOVE_BACK_TO_SIDE.invalidateAll();
		BAKED_QUADS_CACHE.invalidateAll();
	}


	protected final Map<String, Map<BarrelModelPart, BakedModel>> woodModelParts;

	private final ItemOverrides barrelItemOverrides;
	private Item barrelItem = Items.AIR;
	@Nullable
	private String barrelWoodName = null;
	private boolean barrelHasMainColor = false;
	private boolean barrelHasAccentColor = false;
	private boolean barrelIsPacked = false;
	private boolean barrelShowsTier = true;
	private Map<BarrelMaterial, ResourceLocation> barrelMaterials = new EnumMap<>(BarrelMaterial.class);

	private boolean flatTop = false;
	private final Map<String, Map<DynamicBarrelBakingData.DynamicPart, DynamicBarrelBakingData>> woodDynamicBakingData;
	private final Map<String, Map<BarrelModelPart, BakedModel>> woodPartitionedModelParts;
	private final Cache<Integer, BakedModel> dynamicBakedModelCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();

	private ModelBaker baker;
	private Function<Material, TextureAtlasSprite> spriteGetter;

	protected BarrelBakedModelBase(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, Map<String, Map<BarrelModelPart, BakedModel>> woodModelParts, @Nullable BakedModel flatTopModel, Map<String, Map<DynamicBarrelBakingData.DynamicPart, DynamicBarrelBakingData>> woodDynamicBakingData, Map<String, Map<BarrelModelPart, BakedModel>> woodPartitionedModelParts) {
		this.baker = baker;
		this.spriteGetter = spriteGetter;
		this.woodModelParts = woodModelParts;
		barrelItemOverrides = new BarrelItemOverrides(this, flatTopModel);
		this.woodDynamicBakingData = woodDynamicBakingData;
		this.woodPartitionedModelParts = woodPartitionedModelParts;
	}

	private static RenderContext.QuadTransform getDirectionRotationTransform(Direction dir) {
		return QuadTransformers.applying(new Transformation(null, DisplayItemRenderer.getNorthBasedRotation(dir), null, null));
	}

	private RenderContext.QuadTransform getDirectionMoveBackToSide(BlockState state, Direction dir, float distFromCenter, int displayItemIndex, int displayItemCount) {
		int hash = calculateMoveBackToSideHash(state, dir, distFromCenter, displayItemIndex, displayItemCount);
		RenderContext.QuadTransform transform = DIRECTION_MOVE_BACK_TO_SIDE.getIfPresent(hash);
		if (transform == null) {
			Vec3i normal = dir.getNormal();
			Vector3f offset = new Vector3f(distFromCenter, distFromCenter, distFromCenter);
			offset.mul(normal.getX(), normal.getY(), normal.getZ());
			Vector3f frontOffset = DisplayItemRenderer.getDisplayItemIndexFrontOffset(displayItemIndex, displayItemCount);
			frontOffset.add(-0.5f, -0.5f, -0.5f);
			rotateDisplayItemFrontOffset(state, dir, frontOffset);
			frontOffset.add(0.5f, 0.5f, 0.5f);
			offset.add(frontOffset);
			transform = QuadTransformers.applying(new Transformation(offset, null, null, null));

			DIRECTION_MOVE_BACK_TO_SIDE.put(hash, transform);
		}
		return transform;
	}

	@SuppressWarnings("java:S1172") //state used in override
	protected void rotateDisplayItemFrontOffset(BlockState state, Direction dir, Vector3f frontOffset) {
		frontOffset.rotate(getNorthBasedRotation(dir));
	}

	@SuppressWarnings("java:S1172") //state used in override
	protected int calculateMoveBackToSideHash(BlockState state, Direction dir, float distFromCenter, int displayItemIndex, int displayItemCount) {
		int hash = Float.hashCode(distFromCenter);
		hash = 31 * hash + displayItemIndex;
		hash = 31 * hash + displayItemCount;
		return hash;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
		return Collections.emptyList();
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> random, RenderContext context) {
		Object attachment = ((RenderAttachedBlockView) blockView).getBlockEntityRenderAttachment(pos);
		if (!(attachment instanceof BarrelBlockEntity.ModelData extraData)) {
			return;
		}

		emitQuads(state, Boolean.TRUE.equals(extraData.hasMainColor()), Boolean.TRUE.equals(extraData.hasAccentColor()), isPacked(extraData), showsTier(extraData),
				extraData.woodName(), getMaterials(extraData), random, context, extraData);
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<RandomSource> random, RenderContext context) {
		emitQuads(null, barrelHasMainColor, barrelHasAccentColor, barrelIsPacked, barrelShowsTier, barrelWoodName,
				barrelMaterials, random, context, null);
	}

	private void emitQuads(@Nullable BlockState state, boolean hasMainColor, boolean hasAccentColor, boolean isPacked, boolean showsTier, String woodName,
						   Map<BarrelMaterial, ResourceLocation> materials, Supplier<RandomSource> random, RenderContext context, @Nullable BarrelBlockEntity.ModelData extraData) {
		boolean isBakedDynamically = !materials.isEmpty() && woodDynamicBakingData.containsKey(woodName);
		Set<BarrelMaterial.MaterialModelPart> materialModelParts = materials.keySet().stream().map(BarrelMaterial::getMaterialModelPart).collect(Collectors.toSet());
		boolean rendersUsingSplitModel = materialModelParts.contains(BarrelMaterial.MaterialModelPart.CORE) || materialModelParts.contains(BarrelMaterial.MaterialModelPart.TRIM);

		Map<BarrelModelPart, BakedModel> modelParts = getWoodModelParts(woodName, isBakedDynamically && rendersUsingSplitModel);
		if (modelParts.isEmpty()) {
			return;
		}

		if ((!hasMainColor || !hasAccentColor) && !isBakedDynamically) {
			addPartQuads(state, context, modelParts, getBasePart(state));
		}

		addTintableModelQuads(state, context, hasMainColor, hasAccentColor, modelParts);

		if (isBakedDynamically) {
			for (var dir : Direction.values()) {
				bakeAndAddDynamicQuads(dir, random.get(), woodName, materials, rendersUsingSplitModel,
						!hasMainColor || materialModelParts.contains(BarrelMaterial.MaterialModelPart.CORE), !hasAccentColor || materialModelParts.contains(BarrelMaterial.MaterialModelPart.TRIM))
						.forEach(bakedModel -> context.bakedModelConsumer().accept(bakedModel, state));
			}
		}

		if (showsTier) {
			addPartQuads(state, context, modelParts, BarrelModelPart.TIER);
		}

		if (isPacked) {
			addPartQuads(state, context, modelParts, BarrelModelPart.PACKED);
		} else {
			if (showsLocked(extraData)) {
				addPartQuads(state, context, modelParts, BarrelModelPart.LOCKED);
			}
			addDisplayItemQuads(state, context, extraData);
		}
	}

	public List<BakedQuad> getTierQuads(BlockState state, RandomSource rand, String woodName) {
		return getPartQuads(state, rand, woodName, BarrelModelPart.TIER);
	}

	public List<BakedQuad> getLockQuads(BlockState state, RandomSource rand, String woodName) {
		return getPartQuads(state, rand, woodName, BarrelModelPart.LOCKED);
	}

	private List<BakedQuad> getPartQuads(BlockState state, RandomSource rand, String woodName, BarrelModelPart part) {
		List<BakedQuad> ret = new ArrayList<>();

		Map<BarrelModelPart, BakedModel> modelParts = getWoodModelParts(woodName, false);

		for (Direction dir : Direction.values()) {
			addPartQuads(state, dir, rand, ret, modelParts, part);
		}

		return ret;
	}

	private List<BakedModel> bakeAndAddDynamicQuads(@Nullable Direction spriteSide, RandomSource rand, @Nullable String woodName,
			Map<BarrelMaterial, ResourceLocation> barrelMaterials, boolean rendersUsingSplitModel, boolean renderCore, boolean renderTrim) {

		Map<DynamicBarrelBakingData.DynamicPart, DynamicBarrelBakingData> bakingData = woodDynamicBakingData.get(woodName);

		Map<String, Either<Material, String>> materials = new HashMap<>();
		for (Map.Entry<BarrelMaterial, ResourceLocation> entry : barrelMaterials.entrySet()) {
			BarrelMaterial barrelMaterial = entry.getKey();
			ResourceLocation blockName = entry.getValue();
			TextureAtlasSprite sprite = RenderHelper.getSprite(blockName, spriteSide, rand);
			Either<Material, String> material = Either.left(new Material(InventoryMenu.BLOCK_ATLAS, sprite.contents().name()));

			for (BarrelMaterial childMaterial : barrelMaterial.getChildren()) {
				materials.put(childMaterial.getSerializedName(), material);
			}
		}

		List<BakedModel> models = new ArrayList<>();
		if (rendersUsingSplitModel) {
			if (renderCore) {
				models.add(getDynamicModel(woodName, bakingData, materials, DynamicBarrelBakingData.DynamicPart.CORE));
			}
			if (renderTrim) {
				models.add(getDynamicModel(woodName, bakingData, materials, DynamicBarrelBakingData.DynamicPart.TRIM));
			}
		} else {
			models.add(getDynamicModel(woodName, bakingData, materials, DynamicBarrelBakingData.DynamicPart.WHOLE));
		}

		return models;
	}

	private BakedModel getDynamicModel(@Nullable String woodName, Map<DynamicBarrelBakingData.DynamicPart, DynamicBarrelBakingData> bakingData,
			Map<String, Either<Material, String>> materials, DynamicBarrelBakingData.DynamicPart dynamicPart) {
		int hash = Objects.hash(woodName, materials, dynamicPart.name());
		BakedModel bakedModel = dynamicBakedModelCache.getIfPresent(hash);
		if (bakedModel == null) {
			bakedModel = compileAndBakeModel(materials, bakingData.get(dynamicPart));
			dynamicBakedModelCache.put(hash, bakedModel);
		}
		return bakedModel;
	}

	private BlockState getDefaultBlockState(ResourceLocation blockName) {
		Block block = BuiltInRegistries.BLOCK.get(blockName);
		return block != null ? block.defaultBlockState() : Blocks.AIR.defaultBlockState();
	}

	private Map<BarrelMaterial, ResourceLocation> getMaterials(BarrelBlockEntity.ModelData extraData) {
		return extraData.getMaterials() != null ? extraData.getMaterials() : Collections.emptyMap();
	}


	private BakedModel compileAndBakeModel(Map<String, Either<Material, String>> materials, DynamicBarrelBakingData bakingData) {
		bakingData.modelPartDefinition().textures().forEach((textureName, texture) -> {
			if (!materials.containsKey(textureName)) {
				materials.put(textureName, Either.left(texture));
			}
		});

		BarrelDynamicModelBase.BarrelModelPartDefinition baseModelPartDefinition = bakingData.modelPartDefinition();
		return baseModelPartDefinition.modelLocation().map(modelLocation -> {
			BlockModel baseModel = new CompositeElementsModel(modelLocation, materials);
			baseModel.resolveParents(baker::getModel); //need to call resolveParents here to get parent models loaded which happens in that call
			return baseModel.bake(baker, baseModel, spriteGetter, bakingData.modelState(), bakingData.modelLocation(), false);
		}).orElse(Minecraft.getInstance().getModelManager().getMissingModel());
	}

	protected abstract BarrelModelPart getBasePart(@Nullable BlockState state);

	private boolean isPacked(@Nullable BarrelBlockEntity.ModelData extraData) {
		return extraData != null && extraData.isPacked();
	}

	private boolean showsLocked(@Nullable BarrelBlockEntity.ModelData extraData) {
		return extraData != null && extraData.showsLock();
	}

	private boolean showsTier(@Nullable BarrelBlockEntity.ModelData extraData) {
		return extraData != null && extraData.showsTier();
	}


	private int createHash(@Nullable BlockState state, @Nullable Direction side, BarrelBlockEntity.ModelData data) {
		int hash;
		if (state != null) {
			hash = getInWorldBlockHash(state, data);
		} else {
			hash = getItemBlockHash();
		}
		hash = hash * 31 + (side == null ? 0 : side.get3DDataValue() + 1);
		hash = getDisplayItemsHash(data, hash);
		return hash;
	}

	private int getItemBlockHash() {
		int hash = barrelItem.hashCode();
		hash = hash * 31 + (barrelWoodName != null ? barrelWoodName.hashCode() + 1 : 0);
		hash = hash * 31 + (barrelHasMainColor ? 1 : 0);
		hash = hash * 31 + (barrelHasAccentColor ? 1 : 0);
		hash = hash * 31 + (barrelIsPacked ? 1 : 0);
		hash = hash * 31 + (barrelShowsTier ? 1 : 0);
		hash = hash * 31 + (flatTop ? 1 : 0);
		hash = hash * 31 + barrelMaterials.hashCode();
		return hash;
	}

	protected int getInWorldBlockHash(BlockState state, BarrelBlockEntity.ModelData data) {
		int hash = state.getBlock().hashCode();

		//noinspection ConstantConditions
		hash = hash * 31 + (data.woodName() != null ? data.woodName().hashCode() + 1 : 0);
		hash = hash * 31 + (Boolean.TRUE.equals(data.hasMainColor()) ? 1 : 0);
		hash = hash * 31 + (Boolean.TRUE.equals(data.hasAccentColor()) ? 1 : 0);
		hash = hash * 31 + (isPacked(data) ? 1 : 0);
		hash = hash * 31 + (showsLocked(data) ? 1 : 0);
		hash = hash * 31 + (showsTier(data) ? 1 : 0);
		hash = hash * 31 + (Boolean.TRUE.equals(state.getValue(BarrelBlock.FLAT_TOP)) ? 1 : 0);
		//noinspection ConstantConditions
		hash = hash * 31 + (data.getMaterials() != null ? data.getMaterials().hashCode() : 0);
		return hash;
	}

	private int getDisplayItemsHash(BarrelBlockEntity.ModelData data, int hash) {
		if (data.getDisplayItems() != null) {
			List<RenderInfo.DisplayItem> displayItems = data.getDisplayItems();
			//noinspection ConstantConditions
			for (RenderInfo.DisplayItem displayItem : displayItems) {
				hash = hash * 31 + getDisplayItemHash(displayItem);
			}
		}
		if (data.getInaccessibleSlots() != null) {
			List<Integer> inaccessibleSlots = data.getInaccessibleSlots();
			//noinspection ConstantConditions
			for (Integer inaccessibleSlot : inaccessibleSlots) {
				hash = hash * 31 + inaccessibleSlot;
			}
		}
		return hash;
	}

	private int getDisplayItemHash(RenderInfo.DisplayItem displayItem) {
		int hash = displayItem.getRotation();
		hash = hash * 31 + ItemStackKey.getHashCode(displayItem.getItem());
		hash = hash * 31 + displayItem.getSlotIndex();
		return hash;
	}

	private void addDisplayItemQuads(@Nullable BlockState state, RenderContext context, BarrelBlockEntity.ModelData data) {
		if (state == null || !(state.getBlock() instanceof BarrelBlock barrelBlock)) {
			return;
		}

		List<RenderInfo.DisplayItem> displayItems = data.getDisplayItems();

		Minecraft minecraft = Minecraft.getInstance();
		ItemRenderer itemRenderer = minecraft.getItemRenderer();
		if (displayItems != null && !displayItems.isEmpty()) {
			int index = 0;
			for (RenderInfo.DisplayItem displayItem : displayItems) {
				ItemStack item = displayItem.getItem();
				if (barrelBlock.hasFixedIndexDisplayItems()) {
					index = displayItem.getSlotIndex();
				}
				if (item.isEmpty()) {
					continue;
				}

				BakedModel model = itemRenderer.getModel(item, null, minecraft.player, 0);
				if (!model.isCustomRenderer()) {
					int rotation = displayItem.getRotation();
					addRenderedItemSide(state, context, item, model, rotation, index, barrelBlock.getDisplayItemsCount(displayItems));
				}
				index++;
			}
		}

		addInaccessibleSlotsQuads(state, context, data, barrelBlock, displayItems, minecraft);
	}

	private void addInaccessibleSlotsQuads(BlockState state, RenderContext context, BarrelBlockEntity.ModelData data, BarrelBlock barrelBlock,
			@Nullable List<RenderInfo.DisplayItem> displayItems, Minecraft minecraft) {
		List<Integer> inaccessibleSlots = data.getInaccessibleSlots();
		if (displayItems != null && inaccessibleSlots != null) {
			ItemStack inaccessibleSlotStack = new ItemStack(ModItems.INACCESSIBLE_SLOT);
			BakedModel model = minecraft.getItemRenderer().getModel(inaccessibleSlotStack, null, minecraft.player, 0);
			for (int inaccessibleSlot : inaccessibleSlots) {
				if (!model.isCustomRenderer()) {
					addRenderedItemSide(state, context, inaccessibleSlotStack, model, 0, inaccessibleSlot, barrelBlock.getDisplayItemsCount(displayItems));
				}
			}
		}
	}

	@SuppressWarnings({"deprecation", "java:S107"})
	private void addRenderedItemSide(BlockState state, RenderContext context, ItemStack displayItem, BakedModel model, int rotation,
			int displayItemIndex, int displayItemCount) {
		LazyQuadTransformer stack = new LazyQuadTransformer();

		stack.add(MOVE_TO_CORNER);
		stack.add(QuadTransformers.applying(toTransformation(model.getTransforms().getTransform(ItemDisplayContext.FIXED))));
		if (!model.isGui3d()) {
			if (displayItemCount == 1) {
				stack.add(SCALE_BIG_2D_ITEM);
			} else {
				stack.add(SCALE_SMALL_2D_ITEM);
			}
		} else if (displayItemCount > 1) {
			stack.add(SCALE_SMALL_3D_ITEM);
		}

		if (rotation != 0) {
			stack.add(getDisplayRotation(rotation));
		}

		rotateDisplayItemQuads(state, stack);

		Direction facing = state.getBlock() instanceof BarrelBlock barrelBlock ? barrelBlock.getFacing(state) : Direction.NORTH;
		if (model.isGui3d()) {
			stack.add(getDirectionMove(displayItem, model, state, facing, displayItemIndex, displayItemCount, displayItemCount == 1 ? 1 : SMALL_3D_ITEM_SCALE));
		} else {
			stack.add(getDirectionMove(displayItem, model, state, facing, displayItemIndex, displayItemCount, 1));
		}

		stack.add(recalculateDirections());
		stack.add(updateTintIndexes(displayItemIndex));

		context.pushTransform(stack);
		context.bakedModelConsumer().accept(model, state);
		context.popTransform();
	}

	private Transformation toTransformation(ItemTransform transform) {
		if (transform.equals(ItemTransform.NO_TRANSFORM)) {
			return Transformation.identity();
		}

		return new Transformation(transform.translation, TransformationHelper.quatFromXYZ(transform.rotation, true), transform.scale, null);
	}

	protected abstract void rotateDisplayItemQuads(BlockState state, LazyQuadTransformer stack);

	private RenderContext.QuadTransform updateTintIndexes(int displayItemIndex) {
		int offset = (displayItemIndex + 1) * 10;
		return quad -> {
			if (quad.colorIndex() >= 0) {
				quad.colorIndex(quad.colorIndex() + offset);
			}
			return true;
		};
	}

	private RenderContext.QuadTransform recalculateDirections() {
		return quad -> {
			int[] vertexData = new int[VANILLA_QUAD_STRIDE];
			quad.toVanilla(vertexData, 0);
			quad.nominalFace(FaceBakery.calculateFacing(vertexData));
			return true;
		};
	}

	private RenderContext.QuadTransform getDirectionMove(ItemStack displayItem, BakedModel model, BlockState state, Direction direction, int displayItemIndex, int displayItemCount, float itemScale) {
		boolean isFlatTop = state.getValue(BarrelBlock.FLAT_TOP);
		int hash = calculateDirectionMoveHash(state, displayItem, displayItemIndex, displayItemCount, isFlatTop);
		Cache<Integer, RenderContext.QuadTransform> directionCache = DIRECTION_MOVES_3D_ITEMS.getUnchecked(direction);
		RenderContext.QuadTransform transformer = directionCache.getIfPresent(hash);

		if (transformer == null) {
			double offset = DisplayItemRenderer.getDisplayItemOffset(displayItem, model, itemScale);
			if (!isFlatTop) {
				offset -= 1 / 16D;
			}

			transformer = getDirectionMoveBackToSide(state, direction, (float) (0.5f + offset), displayItemIndex, displayItemCount);
			directionCache.put(hash, transformer);
		}

		return transformer;
	}

	@SuppressWarnings("java:S1172") //state used in override
	protected int calculateDirectionMoveHash(BlockState state, ItemStack displayItem, int displayItemIndex, int displayItemCount, boolean isFlatTop) {
		int hashCode = ItemStackKey.getHashCode(displayItem);
		hashCode = hashCode * 31 + displayItemIndex;
		hashCode = hashCode * 31 + displayItemCount;
		hashCode = hashCode * 31 + (isFlatTop ? 1 : 0);
		return hashCode;
	}

	private RenderContext.QuadTransform getDisplayRotation(int rotation) {
		return DISPLAY_ROTATIONS.computeIfAbsent(rotation, r -> QuadTransformers.applying(new Transformation(null, Axis.ZP.rotationDegrees(rotation), null, null)));
	}

	private void addTintableModelQuads(@Nullable BlockState state, RenderContext context, boolean hasMainColor,
									   boolean hasAccentColor, Map<BarrelModelPart, BakedModel> modelParts) {
		if (hasAccentColor) {
			addPartQuads(state, context, modelParts, BarrelModelPart.TINTABLE_ACCENT);
		}

		if (hasMainColor) {
			addPartQuads(state, context, modelParts, getMainPart(state));
		}
	}

	private BarrelModelPart getMainPart(@Nullable BlockState state) {
		return rendersOpen() && state != null && Boolean.TRUE.equals(state.getValue(BarrelBlock.OPEN)) ? BarrelModelPart.TINTABLE_MAIN_OPEN : BarrelModelPart.TINTABLE_MAIN;
	}

	protected abstract boolean rendersOpen();

	private void addPartQuads(@Nullable BlockState state, RenderContext context, Map<BarrelModelPart, BakedModel> modelParts, BarrelModelPart part) {
		if (modelParts.containsKey(part)) {
			context.bakedModelConsumer().accept(modelParts.getOrDefault(part, Minecraft.getInstance().getModelManager().getMissingModel()), state);
		}
	}

	private void addPartQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, List<BakedQuad> ret,
							  Map<BarrelModelPart, BakedModel> modelParts, BarrelModelPart part) {
		if (modelParts.containsKey(part)) {
			ret.addAll(modelParts.getOrDefault(part, Minecraft.getInstance().getModelManager().getMissingModel()).getQuads(state, side, rand));
		}
	}

	private Map<BarrelModelPart, BakedModel> getWoodModelParts(@Nullable String barrelWoodName, boolean requiresPartitionedModel) {
		if (requiresPartitionedModel && woodPartitionedModelParts.containsKey(barrelWoodName)) {
			return woodPartitionedModelParts.get(barrelWoodName);
		} else {
			if (woodModelParts.isEmpty()) {
				return Collections.emptyMap();
			} else if (barrelWoodName == null || !woodModelParts.containsKey(barrelWoodName)) {
				return woodModelParts.values().iterator().next();
			} else {
				return woodModelParts.get(barrelWoodName);
			}
		}
	}

	@Override
	public boolean useAmbientOcclusion() {
		return false; //because occlusion calculation makes display item dark on faces that are exposed to light
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	@Override
	public boolean usesBlockLight() {
		return true;
	}

	@Override
	public boolean isCustomRenderer() {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public TextureAtlasSprite getParticleIcon() {
		return getWoodModelParts(null, false).getOrDefault(BarrelModelPart.BASE, Minecraft.getInstance().getModelManager().getMissingModel()).getParticleIcon();
	}

	@SuppressWarnings("deprecation")
	@Override
	public ItemTransforms getTransforms() {
		return ITEM_TRANSFORMS;
	}

	@Override
	public TextureAtlasSprite getParticleIcon(BlockState state, BlockAndTintGetter blockView, BlockPos pos) {
		Object attachment = ((RenderAttachedBlockView) blockView).getBlockEntityRenderAttachment(pos);
		if (attachment instanceof BarrelBlockEntity.ModelData data) {
			if (data.hasMainColor() && data.hasMainColor()) {
				BakedModel model = getWoodModelParts(null, false).get(BarrelModelPart.TINTABLE_MAIN);
				if (model instanceof IDataModel dataModel) {
					return dataModel.getParticleIcon(state, blockView, pos);
				}
				return model.getParticleIcon();
			}

			if (data.getMaterials() != null) {
				Map<BarrelMaterial, ResourceLocation> materials = data.getMaterials();
				if (materials != null) {
					for (BarrelMaterial barrelMaterial : PARTICLE_ICON_MATERIAL_PRIORITY) {
						if (materials.containsKey(barrelMaterial)) {
							BlockState blockState = getDefaultBlockState(materials.get(barrelMaterial));
							return Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState).getParticleIcon();
						}
					}
				}
			}

			if (data.woodName() != null) {
				String name = data.woodName();
				if (!woodModelParts.containsKey(name)) {
					return getParticleIcon();
				}
				BakedModel model = getWoodModelParts(null, false).get(BarrelModelPart.BASE);
				if (model instanceof IDataModel dataModel) {
					return dataModel.getParticleIcon(state, blockView, pos);
				}
				return model.getParticleIcon();
			}

		}

		return getParticleIcon();
	}

	@Override
	public ItemOverrides getOverrides() {
		return barrelItemOverrides;
	}

/*	@Override
	public BakedModel applyTransform(ItemTransforms.TransformType transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
		if (transformType == ItemTransforms.TransformType.NONE) {
			return this;
		}

		ITEM_TRANSFORMS.getTransform(transformType).apply(applyLeftHandTransform, poseStack);

		return this;
	}*/

	private static class BarrelItemOverrides extends ItemOverrides {
		private final BarrelBakedModelBase barrelBakedModel;
		@Nullable
		private final BakedModel flatTopModel;

		public BarrelItemOverrides(BarrelBakedModelBase barrelBakedModel, @Nullable BakedModel flatTopModel) {
			this.barrelBakedModel = barrelBakedModel;
			this.flatTopModel = flatTopModel;
		}

		@Nullable
		@Override
		public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
			boolean flatTop = BarrelBlockItem.isFlatTop(stack);
			if (flatTopModel != null && flatTop) {
				return flatTopModel.getOverrides().resolve(flatTopModel, stack, level, entity, seed);
			}

			barrelBakedModel.barrelHasMainColor = StorageBlockItem.getMainColorFromStack(stack).isPresent();
			barrelBakedModel.barrelHasAccentColor = StorageBlockItem.getAccentColorFromStack(stack).isPresent();
			barrelBakedModel.barrelWoodName = WoodStorageBlockItem.getWoodType(stack).map(WoodType::name)
					.orElse(barrelBakedModel.barrelHasAccentColor && barrelBakedModel.barrelHasMainColor ? null : WoodType.ACACIA.name());
			barrelBakedModel.barrelIsPacked = WoodStorageBlockItem.isPacked(stack);
			barrelBakedModel.barrelShowsTier = StorageBlockItem.showsTier(stack);
			barrelBakedModel.barrelItem = stack.getItem();
			barrelBakedModel.flatTop = flatTop;
			barrelBakedModel.barrelMaterials = BarrelBlockItem.getMaterials(stack);
			return barrelBakedModel;
		}
	}
}
