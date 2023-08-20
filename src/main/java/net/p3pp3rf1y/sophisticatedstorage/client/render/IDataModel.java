package net.p3pp3rf1y.sophisticatedstorage.client.render;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface IDataModel {
    TextureAtlasSprite getParticleIcon(BlockState state, BlockAndTintGetter blockView, BlockPos pos);
}
