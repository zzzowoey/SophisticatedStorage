package net.p3pp3rf1y.sophisticatedstorage.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.p3pp3rf1y.sophisticatedstorage.client.render.IDataModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {
    @Unique
    private static BlockPos pos = null;

    @Redirect(method = "renderScreenEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/BlockModelShaper;getParticleIcon(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"))
    private static TextureAtlasSprite sophisticatedstorage$renderEffectScreen$getParticleIcon(BlockModelShaper instance, BlockState state) {
        if (instance.getBlockModel(state) instanceof IDataModel model && pos != null) {
            Minecraft mc = Minecraft.getInstance();
            TextureAtlasSprite sprite = model.getParticleIcon(state, mc.level, pos);
            pos = null;
            return sprite;
        }
        return instance.getParticleIcon(state);
    }

    @Inject(method = "getViewBlockingState", at = @At(value = "RETURN", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void sophisticatedstorage$getViewBlockingState(Player player, CallbackInfoReturnable<BlockState> cir, BlockPos.MutableBlockPos mutableBlockPos) {
        if (cir.getReturnValue() != null) {
            pos = mutableBlockPos.immutable();
        }
    }
}
