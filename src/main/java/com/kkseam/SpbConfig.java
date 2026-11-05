package com.kkseam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.neoforged.fml.loading.FMLPaths;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpbConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("spbtrainer.json");
    private static ConfigData config = new ConfigData();
    private static final Map<UUID, Integer> PREVIEW_TIME_SECONDS = new HashMap<UUID, Integer>();
    private static final Map<UUID, Boolean> PENDING_CUSTOM_INPUT = new HashMap<UUID, Boolean>();

    public static void resetBestTime(UUID player, String buildName) {
        PlayerSettings settings = SpbConfig.getPlayerSettings(player);
        settings.bestTimes.remove(buildName);
        SpbConfig.saveConfig();
    }

    public static synchronized void loadConfig() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                return;
            }
            try (BufferedReader reader = Files.newBufferedReader(CONFIG_PATH)) {
                ConfigData loaded = GSON.fromJson(reader, ConfigData.class);
                if (loaded != null) {
                    config = loaded;
                }
            }
        } catch (Exception e) {
            SpbTrainerMod.LOGGER.error("Failed to load SPB config", e);
        }
    }

    public static synchronized void saveConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_PATH);){
                GSON.toJson((Object)config, (Appendable)writer);
            }
        }
        catch (Exception e) {
            SpbTrainerMod.LOGGER.error("Failed to save SPB config", e);
        }
    }

    public static PlayerSettings getPlayerSettings(UUID uuid) {
        return SpbConfig.config.players.computeIfAbsent(uuid, k -> new PlayerSettings());
    }

    public static GlobalSettings getGlobalSettings() {
        return SpbConfig.config.globalSettings;
    }

    public static void setPreview(UUID uuid, boolean enabled) {
        SpbConfig.getPlayerSettings(uuid).previewEnabled = enabled;
        SpbConfig.saveConfig();
    }

    public static void setPreviewEnabled(UUID uuid, boolean enabled) {
        SpbConfig.setPreview(uuid, enabled);
    }

    public static boolean togglePreview(UUID uuid) {
        PlayerSettings s = SpbConfig.getPlayerSettings(uuid);
        s.previewEnabled = !s.previewEnabled;
        SpbConfig.saveConfig();
        return s.previewEnabled;
    }

    public static boolean isPreviewEnabled(UUID uuid) {
        return SpbConfig.getPlayerSettings(uuid).previewEnabled;
    }

    public static Long getBestTime(UUID player, String buildName) {
        return SpbConfig.getPlayerSettings(player).bestTimes.get(buildName);
    }

    public static void setBestTime(UUID player, String buildName, long timeMs) {
        SpbConfig.getPlayerSettings(player).bestTimes.put(buildName, timeMs);
        SpbConfig.saveConfig();
    }

    public static int getPreviewTime(UUID playerUuid) {
        return PREVIEW_TIME_SECONDS.getOrDefault(playerUuid, 5);
    }

    public static void setPreviewTime(UUID playerUuid, int seconds) {
        PREVIEW_TIME_SECONDS.put(playerUuid, Math.max(0, Math.min(60, seconds)));
    }

    public static boolean isPendingCustomInput(UUID playerUuid) {
        return PENDING_CUSTOM_INPUT.getOrDefault(playerUuid, false);
    }

    public static void setPendingCustomInput(UUID playerUuid, boolean pending) {
        if (pending) {
            PENDING_CUSTOM_INPUT.put(playerUuid, true);
        } else {
            PENDING_CUSTOM_INPUT.remove(playerUuid);
        }
    }

    public static boolean isSignSuppressionEnabled(UUID playerUuid) {
        return SpbConfig.getGlobalSettings().enableSignSuppression && SpbConfig.getPlayerSettings(playerUuid).signTextSuppression;
    }

    public static void setSignSuppression(UUID playerUuid, boolean enabled) {
        SpbConfig.getPlayerSettings(playerUuid).signTextSuppression = enabled;
        SpbConfig.saveConfig();
    }

    public static boolean toggleSignSuppression(UUID playerUuid) {
        PlayerSettings s = SpbConfig.getPlayerSettings(playerUuid);
        s.signTextSuppression = !s.signTextSuppression;
        SpbConfig.saveConfig();
        return s.signTextSuppression;
    }

    public static void setGlobalSignSuppression(boolean enabled) {
        SpbConfig.getGlobalSettings().enableSignSuppression = enabled;
        SpbConfig.saveConfig();
    }

    public static boolean isGlobalSignSuppressionEnabled() {
        return SpbConfig.getGlobalSettings().enableSignSuppression;
    }

    public static String getFinishTitle(UUID uuid) {
        String t = SpbConfig.getPlayerSettings(uuid).finishTitle;
        return t == null ? "Superb!" : t;
    }

    public static String getFinishColor(UUID uuid) {
        String c = SpbConfig.getPlayerSettings(uuid).finishColor;
        return c == null ? "green" : c;
    }

    public static void setFinishTitle(UUID uuid, String text) {
        SpbConfig.getPlayerSettings(uuid).finishTitle = text;
        SpbConfig.saveConfig();
    }

    public static void setFinishColor(UUID uuid, String color) {
        SpbConfig.getPlayerSettings(uuid).finishColor = color;
        SpbConfig.saveConfig();
    }

    public static class PlayerSettings {
        public boolean previewEnabled = true;
        public boolean showTutorial = true;
        public String customColor = "green";
        public Map<String, Long> bestTimes = new HashMap<String, Long>();
        public boolean signTextSuppression = true;
        public String finishTitle = "Superb!";
        public String finishColor = "green";
    }

    public static class ConfigData {
        public Map<UUID, PlayerSettings> players = new HashMap<UUID, PlayerSettings>();
        public GlobalSettings globalSettings = new GlobalSettings();
    }

    public static class GlobalSettings {
        public String preferredBuild = "spbtest";
        public boolean announcePlacements = true;
        public boolean enableSignSuppression = true;
    }
}

