package com.kkseam;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.UUID;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;

// Client-only half of the mod (keybinds + sign-editor suppression).
@Mod(value = SpbTrainerMod.MODID, dist = Dist.CLIENT)
public class SpbTrainerClient {
    private static KeyMapping resetKeybind;
    private static KeyMapping restartKeybind;

    public SpbTrainerClient(IEventBus modEventBus) {
        modEventBus.addListener(SpbTrainerClient::onRegisterKeys);
        NeoForge.EVENT_BUS.addListener(SpbTrainerClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(SpbTrainerClient::onScreenInit);
    }

    private static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        resetKeybind = new KeyMapping("key.spbtrainer.reset", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT, "key.categories.spbtrainer");
        restartKeybind = new KeyMapping("key.spbtrainer.restart", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F6, "key.categories.spbtrainer");
        event.register(resetKeybind);
        event.register(restartKeybind);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (resetKeybind != null && resetKeybind.consumeClick() && mc.player != null) {
            mc.player.connection.sendCommand("spb reset");
        }
        if (restartKeybind != null && restartKeybind.consumeClick() && mc.player != null) {
            mc.player.connection.sendCommand("spb restart");
        }
    }

    private static void onScreenInit(ScreenEvent.Init.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (event.getScreen() instanceof AbstractSignEditScreen && mc.player != null) {
            try {
                UUID playerUuid = mc.player.getUUID();
                if (SpbConfig.isSignSuppressionEnabled(playerUuid)) {
                    mc.execute(() -> mc.execute(() -> {
                        if (mc.screen instanceof AbstractSignEditScreen) {
                            mc.setScreen(null);
                        }
                    }));
                }
            } catch (Exception ignored) {
            }
        }
    }
}
