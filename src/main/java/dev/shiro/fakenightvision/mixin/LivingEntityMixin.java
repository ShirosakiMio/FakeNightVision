package dev.shiro.fakenightvision.mixin;

import dev.shiro.fakenightvision.Config;
import dev.shiro.fakenightvision.client.NightVisionState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 客户端效果判定：对本地玩家与相机实体强制夜视为生效、黑暗效果为不存在。
 * 参考 LivingEntityMixin（https://github.com/Fallen-Breath/tweakermore/blob/master/src/main/java/me/fallenbreath/tweakermore/mixins/tweaks/mc_tweaks/fakeNightVision/LivingEntityMixin.java）
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(
            method = "hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void fakenightvision$rewriteEffects(MobEffect effect, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        Minecraft mc = Minecraft.getInstance();
        if (self != mc.player && self != mc.gameRenderer.getMainCamera().getEntity()) {
            return;
        }
        if (effect == MobEffects.NIGHT_VISION) {
            if (NightVisionState.isEffectivelyEnabled()) {
                cir.setReturnValue(true);
            }
        } else if (effect == MobEffects.DARKNESS && Config.isDarknessRemoved()) {
            cir.setReturnValue(false);
        }
    }
}
