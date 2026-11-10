package dev.shiro.fakenightvision.client;

import dev.shiro.fakenightvision.Config;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;

/**
 * 伪夜视的客户端运行时状态：AUTO 模式的光照判定与渲染强度的平滑渐变。
 */
public final class NightVisionState {

    /** 渲染强度渐变时长（毫秒） */
    private static final long FADE_MS = 500;
    /** 自动模式回滞区间：亮度高于阈值至少该值时才关闭，避免在阈值附近反复切换 */
    private static final int HYSTERESIS = 2;
    /** 自动模式的光照采样间隔（tick） */
    private static final int SAMPLE_INTERVAL_TICKS = 10;

    private static boolean autoActive;
    private static boolean lastEffective;
    private static long stateChangedAtMillis;
    private static int sampleCountdown;

    private NightVisionState() {
    }

    /**
     * 伪夜视当前是否实际生效（AUTO 模式取最近一次光照判定结果）。
     */
    public static boolean isEffectivelyEnabled() {
        return switch (Config.getMode()) {
            case ON -> true;
            case AUTO -> autoActive;
            case OFF -> false;
        };
    }

    /**
     * 当前渲染强度，生效状态翻转后在固定时长内线性过渡到目标值。
     */
    public static float getRenderScale() {
        boolean effective = isEffectivelyEnabled();
        if (effective != lastEffective) {
            lastEffective = effective;
            stateChangedAtMillis = Util.getMillis();
        }
        float progress = Mth.clamp((Util.getMillis() - stateChangedAtMillis) / (float) FADE_MS, 0.0F, 1.0F);
        return effective ? progress : 1.0F - progress;
    }

    /**
     * 每隔固定间隔采样玩家所在格的等效亮度，在 AUTO 模式下带回滞地驱动伪夜视开关。
     */
    public static void tickAuto(Minecraft mc) {
        if (Config.getMode() != Config.Mode.AUTO || --sampleCountdown > 0) {
            return;
        }
        sampleCountdown = SAMPLE_INTERVAL_TICKS;
        int light = sampleEffectiveLight(mc);
        if (light < 0) {
            return;
        }
        int threshold = Config.getAutoLightThreshold();
        if (light <= threshold) {
            autoActive = true;
        } else if (light >= threshold + HYSTERESIS) {
            autoActive = false;
        }
    }

    /**
     * 采样玩家所在格的等效亮度：方块光与扣除天光暗度后的天空光取较大值；无玩家或世界时返回 -1。
     */
    public static int sampleEffectiveLight(Minecraft mc) {
        LocalPlayer player = mc.player;
        ClientLevel level = mc.level;
        if (player == null || level == null) {
            return -1;
        }
        BlockPos pos = player.blockPosition();
        int block = level.getBrightness(LightLayer.BLOCK, pos);
        int sky = level.getBrightness(LightLayer.SKY, pos);
        return Math.max(block, sky - computeSkyDarken(level));
    }

    /**
     * 计算天光暗度（0~11，夜晚与雷雨天气更高）。原版只在服务端和 ClientLevel 构造时刷新该值，
     * 客户端运行期间保持进世界时的快照，因此按原版 Level#updateSkyBrightness 的公式实时计算。
     */
    private static int computeSkyDarken(ClientLevel level) {
        double rain = 1.0D - (double) (level.getRainLevel(1.0F) * 5.0F) / 16.0D;
        double thunder = 1.0D - (double) (level.getThunderLevel(1.0F) * 5.0F) / 16.0D;
        double day = 0.5D + 2.0D * Mth.clamp(Mth.cos(level.getTimeOfDay(1.0F) * 6.2831855F), -0.25D, 0.25D);
        return (int) ((1.0D - day * rain * thunder) * 11.0D);
    }
}
