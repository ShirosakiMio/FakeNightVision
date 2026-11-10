package dev.shiro.fakenightvision;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * 伪夜视的客户端配置，状态持久化在 fakenightvision-client.toml。
 */
public final class Config {

    /**
     * 伪夜视工作模式。
     */
    public enum Mode {
        /** 关闭 */
        OFF,
        /** 手动常开 */
        ON,
        /** 按环境亮度自动开关 */
        AUTO
    }

    public static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.EnumValue<Mode> MODE;
    private static final ForgeConfigSpec.IntValue AUTO_LIGHT_THRESHOLD;
    private static final ForgeConfigSpec.BooleanValue REMOVE_DARKNESS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("general");
        MODE = builder
                .comment("伪夜视模式：OFF=关闭，ON=常开，AUTO=按环境亮度自动开关")
                .defineEnum("mode", Mode.OFF);
        AUTO_LIGHT_THRESHOLD = builder
                .comment("AUTO 模式的亮度阈值：等效亮度 <= 阈值时开启，>= 阈值+2 时关闭（0~15）")
                .defineInRange("autoLightThreshold", 6, 0, 15);
        REMOVE_DARKNESS = builder
                .comment("消除监守者与幽匿尖啸体施加的黑暗效果")
                .define("removeDarkness", true);
        builder.pop();
        SPEC = builder.build();
    }

    private Config() {
    }

    public static Mode getMode() {
        return MODE.get();
    }

    /**
     * 设置模式并写回配置文件。
     */
    public static void setMode(Mode mode) {
        MODE.set(mode);
        SPEC.save();
    }

    public static int getAutoLightThreshold() {
        return AUTO_LIGHT_THRESHOLD.get();
    }

    public static boolean isDarknessRemoved() {
        return REMOVE_DARKNESS.get();
    }
}
