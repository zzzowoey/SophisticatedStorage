package net.p3pp3rf1y.sophisticatedstorage.client.render;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.p3pp3rf1y.sophisticatedstorage.SophisticatedStorage;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class CompositeElementsModel extends BlockModel {
	private final List<BlockElement> elements;

	public CompositeElementsModel(@Nullable ResourceLocation parentLocation, Map<String, Either<Material, String>> textureMap) {
		super(parentLocation, Collections.emptyList(), textureMap, true, null, ItemTransforms.NO_TRANSFORMS, Collections.emptyList());
		elements = new ArrayList<>();
	}

	@Override
	public BakedModel bake(ModelBaker baker, BlockModel model, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state, ResourceLocation location, boolean guiLight3d) {
		var particleSprite = spriteGetter.apply(getMaterial("particle"));
		if (getRootModel() == ModelBakery.BLOCK_ENTITY_MARKER) {
			return new BuiltInModel(getTransforms(), getOverrides(baker, model, spriteGetter), particleSprite, getGuiLight().lightLikeBlock());
		}

		ItemOverrides overrides = getOverrides(baker, model, spriteGetter);
		ItemTransforms transforms = this.getTransforms();
		var modelBuilder = SimpleCompositeModel.Baked.builder(this.hasAmbientOcclusion(), false, this.getGuiLight().lightLikeBlock(), particleSprite, overrides, transforms);
		for (BlockElement element : getElements()) {
			element.faces.forEach((side, face) -> {
				var sprite = spriteGetter.apply(this.getMaterial(face.texture));
				var simpleModelBuilder = new SimpleBakedModel.Builder(this.hasAmbientOcclusion(), this.getGuiLight().lightLikeBlock(), false, transforms, overrides).particle(sprite);
				simpleModelBuilder.addUnculledFace(BlockModel.FACE_BAKERY.bakeQuad(element.from, element.to, face, sprite, side, state, element.rotation, element.shade, location));
				modelBuilder.addLayer(simpleModelBuilder.build());
			});
		}
		return modelBuilder.build();
	}

	@SuppressWarnings({"java:S1874", "deprecation"}) //overriding getElements here
	@Override
	public List<BlockElement> getElements() {
		return elements;
	}

	@Override
	public void resolveParents(Function<ResourceLocation, UnbakedModel> models) {
		super.resolveParents(models);

		copyElementsFromAllIncludedModels();
		copyTexturesFromAllIncludedModels();
	}

	@SuppressWarnings({"java:S1874", "deprecation"}) //need to call getElements even though deprecated
	private void copyElementsFromAllIncludedModels() {
		if (parent != null) {
			elements.addAll(parent.getElements());
			if (parent.getCustomGeometry() instanceof SimpleCompositeModel simpleCompositeModel) {
				elements.addAll(simpleCompositeModel.getElements());
			}
		}
	}

	@SuppressWarnings("java:S5803") //need to call textureMap here even though only visible for testing
	private void copyTexturesFromAllIncludedModels() {
		if (parent != null) {
			parent.textureMap.forEach(textureMap::putIfAbsent);
			if (parent.getCustomGeometry() instanceof SimpleCompositeModel simpleCompositeModel) {
				simpleCompositeModel.getTextures().forEach(textureMap::putIfAbsent);
			}
		}
	}

	@Override
	public Material getMaterial(String textureName) {
		if (textureName.charAt(0) == '#') {
			textureName = textureName.substring(1);
		}

		List<String> visitedTextureReferences = Lists.newArrayList();
		while (true) {
			Either<Material, String> either = findTexture(textureName);
			Optional<Material> optional = either.left();
			if (optional.isPresent()) {
				return optional.get();
			}

			textureName = either.right().orElse("");

			if (visitedTextureReferences.contains(textureName)) {
				String finalTextureName = textureName;
				SophisticatedStorage.LOGGER.warn("Unable to resolve texture due to reference chain {}->{} in {}", () -> Joiner.on("->").join(visitedTextureReferences), () -> finalTextureName, () -> name);
				return new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation());
			}

			visitedTextureReferences.add(textureName);
		}
	}

	private Either<Material, String> findTexture(String textureName) {
		for (BlockModel blockmodel = this; blockmodel != null; blockmodel = blockmodel.parent) {
			Either<Material, String> either = blockmodel.textureMap.get(textureName);
			if (either != null) {
				return either;
			}
		}

		return Either.left(new Material(InventoryMenu.BLOCK_ATLAS, MissingTextureAtlasSprite.getLocation()));
	}
}
