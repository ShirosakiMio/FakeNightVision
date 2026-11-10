package dev.shiro.fakenightvision.client;

import dev.shiro.fakenightvision.Config;
import dev.shiro.fakenightvision.Config.Mode;
import dev.shiro.fakenightvision.FakeNightVision;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 客户端按键处理：循环切换伪夜视模式（关闭 → 常开 → 自动）并在状态栏提示。
 */
@Mod.EventBusSubscriber(modid = FakeNightVision.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ClientTickHandler {

    private ClientTickHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        while (ClientSetup.TOGGLE_KEY.consumeClick()) {
            Mode mode = switch (Config.getMode()) {
                case OFF -> Mode.ON;
                case ON -> Mode.AUTO;
                case AUTO -> Mode.OFF;
            };
            Config.setMode(mode);
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                Component message = mode == Mode.AUTO
                        ? Component.translatable("message.fakenightvision.mode.auto",
                                NightVisionState.sampleEffectiveLight(Minecraft.getInstance()))
                        : Component.translatable(
                                "message.fakenightvision.mode." + mode.name().toLowerCase(Locale.ROOT));
                player.displayClientMessage(message, true);
            }
        }
        NightVisionState.tickAuto(Minecraft.getInstance());
    }
}
