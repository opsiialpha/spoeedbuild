package com.kkseam;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import org.slf4j.Logger;

@Mod(SpbTrainerMod.MODID)
public class SpbTrainerMod {
    public static final String MODID = "spbtrainer";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SpbTrainerMod(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.addListener(SpbTrainerMod::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(SpbTrainerMod::onServerChat);
        buildMode.registerFluidEvents();
        buildMode.registerEvents();
        buildLoader.registerScheduler();
        SpbConfig.loadConfig();
        buildLoader.registerAccuracyPoller();
        LOGGER.info("SPB Trainer initialized successfully!");
    }

    private static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        commands.register(dispatcher);
    }

    private static void onServerChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        if (!SpbConfig.isPendingCustomInput(player.getUUID())) {
            return;
        }
        try {
            int seconds = Integer.parseInt(event.getRawText());
            if (seconds >= 0 && seconds <= 60) {
                SpbConfig.setPreviewTime(player.getUUID(), seconds);
                SpbConfig.setPendingCustomInput(player.getUUID(), false);
                player.displayClientMessage(Component.literal("\u00a7aPreview time set to " + seconds + " seconds!"), false);
                commands.showConfig(player.createCommandSourceStack(), player);
            } else {
                player.displayClientMessage(Component.literal("\u00a7cInvalid time! Enter a number between 0 and 60."), false);
            }
        } catch (NumberFormatException e) {
            player.displayClientMessage(Component.literal("\u00a7cInvalid number! Enter a number between 0 and 60."), false);
        }
    }
}
