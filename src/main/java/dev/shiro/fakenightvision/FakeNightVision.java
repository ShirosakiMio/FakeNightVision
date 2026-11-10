package dev.shiro.fakenightvision;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * 伪夜视：无需夜视药水效果，强制让客户端以夜视亮度渲染世界。
 */
@Mod(FakeNightVision.MOD_ID)
public final class FakeNightVision {
    public static final String MOD_ID = "fakenightvision";

    public FakeNightVision() {
        // 纯客户端模组，CLIENT 配置仅在客户端注册
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
        }
    }
}
