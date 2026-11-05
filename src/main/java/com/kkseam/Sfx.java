package com.kkseam;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

/** Hypixel / Bedwars style sound cues, played only to the player. */
public class Sfx {
    private static void play(ServerPlayer player, SoundEvent sound, float volume, float pitch) {
        if (player == null || sound == null) return;
        player.playNotifySound(sound, SoundSource.MASTER, volume, pitch);
    }

    /** Chat "Starting in N..." tick (Hypixel lobby-countdown click). */
    public static void chatTick(ServerPlayer player) {
        play(player, SoundEvents.NOTE_BLOCK_HAT.value(), 1.0f, 1.0f);
    }

    /** On-screen countdown number (5, 4, 3, 2, 1). The last number is higher pitched. */
    public static void countdownTick(ServerPlayer player, boolean last) {
        play(player, SoundEvents.NOTE_BLOCK_PLING.value(), 1.0f, last ? 1.5f : 1.0f);
    }

    /** Countdown reached zero: build mode starts, go build. */
    public static void buildStart(ServerPlayer player) {
        play(player, SoundEvents.PLAYER_LEVELUP, 1.0f, 1.0f);
    }

    /** Build finished at 100% accuracy. */
    public static void buildFinish(ServerPlayer player) {
        play(player, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
    }
}
