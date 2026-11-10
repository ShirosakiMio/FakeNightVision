package dev.shiro.fakenightvision.mixin;

import dev.shiro.fakenightvision.client.NightVisionState;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 伪夜视：将光照管线取到的夜视强度强制为本模组的实际状态（含切换时的平滑渐变）。
 * 参考 GameRendererMixin（https://github.com/Fallen-Breath/tweakermore/blob/master/src/main/java/me/fallenbreath/tweakermore/mixins/tweaks/mc_tweaks/fakeNightVision/GameRendererMixin.java）
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(
            method = "getNightVisionScale(Lnet/minecraft/world/entity/LivingEntity;F)F",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void fakenightvision$forceStrength(CallbackInfoReturnable<Float> cir) {
        float scale = NightVisionState.getRenderScale();
        if (scale > 0.0F) {
            cir.setReturnValue(scale);
        }
    }
}
