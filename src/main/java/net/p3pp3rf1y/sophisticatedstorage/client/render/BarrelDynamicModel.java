package net.p3pp3rf1y.sophisticatedstorage.client.render;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.p3pp3rf1y.sophisticatedstorage.block.BarrelBlock;
import net.p3pp3rf1y.sophisticatedstorage.block.BarrelBlockEntity;
import net.p3pp3rf1y.sophisticatedstorage.client.util.LazyQuadTransformer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class BarrelDynamicModel extends BarrelDynamicModelBase<BarrelDynamicModel> {

	public BarrelDynamicModel(@Nullable ResourceLocation parentLocation, Map<String, Map<BarrelModelPart, BarrelModelPartDefinition>> woodOverrides, @Nullable ResourceLocation flatTopModelName, Map<DynamicBarrelBakingData.DynamicPart, ResourceLocation> dynamicPartModels, Map<String, Map<BarrelModelPart, BarrelModelPartDefinition>> woodPartitionedModelPartDefinitions) {
		super(parentLocation, woodOverrides, flatTopModelName, dynamicPartModels, woodPartitionedModelPartDefinitions);
	}

	@Override
	protected BarrelBakedModelBase instantiateBakedModel(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, Map<String, Map<BarrelModelPart, BakedModel>> woodModelParts, @Nullable BakedModel flatTopModel, Map<String, Map<DynamicBarrelBakingData.DynamicPart, DynamicBarrelBakingData>> woodDynamicBakingData, Map<String, Map<BarrelModelPart, BakedModel>> woodPartitionedModelParts) {
		return new BarrelBakedModel(baker, spriteGetter, woodModelParts, flatTopModel, woodDynamicBakingData, woodPartitionedModelParts);
	}

	private static class BarrelBakedModel extends BarrelBakedModelBase {
		public BarrelBakedModel(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, Map<String, Map<BarrelModelPart, BakedModel>> woodModelParts, @Nullable BakedModel flatTopModel, Map<String, Map<DynamicBarrelBakingData.DynamicPart, DynamicBarrelBakingData>> woodDynamicBakingData, Map<String, Map<BarrelModelPart, BakedModel>> woodPartitionedModelParts) {
			super(baker, spriteGetter, woodModelParts, flatTopModel, woodDynamicBakingData, woodPartitionedModelParts);
		}

		@Override
		protected BarrelModelPart getBasePart(@Nullable BlockState state) {
			return state != null && state.getValue(BarrelBlock.OPEN) ? BarrelModelPart.BASE_OPEN : BarrelModelPart.BASE;
		}

		@Override
		protected void rotateDisplayItemQuads(BlockState state, LazyQuadTransformer stack) {
			stack.add(DIRECTION_ROTATES.get(state.getValue(BarrelBlock.FACING)));
		}

		@Override
		protected boolean rendersOpen() {
			return true;
		}

		@Override
		protected int calculateMoveBackToSideHash(BlockState state, Direction dir, float distFromCenter, int displayItemIndex, int displayItemCount) {
			int hash = super.calculateMoveBackToSideHash(state, dir, distFromCenter, displayItemIndex, displayItemCount);
			hash = 31 * hash + dir.hashCode();
			return hash;
		}
	}

	@SuppressWarnings("java:S6548") //singleton is intended here
	public static final class Loader extends BarrelDynamicModelBase.Loader<BarrelDynamicModel> {
		public static final BarrelDynamicModel.Loader INSTANCE = new BarrelDynamicModel.Loader();

		@Override
		protected BarrelDynamicModel instantiateModel(@Nullable ResourceLocation parentLocation, Map<String, Map<BarrelModelPart, BarrelModelPartDefinition>> woodOverrides, @Nullable ResourceLocation flatTopModelName, Map<DynamicBarrelBakingData.DynamicPart, ResourceLocation> dynamicPartModels, Map<String, Map<BarrelModelPart, BarrelModelPartDefinition>> woodPartitionedModelPartDefinitions) {
			return new BarrelDynamicModel(parentLocation, woodOverrides, flatTopModelName, dynamicPartModels, woodPartitionedModelPartDefinitions);
		}
	}
}
