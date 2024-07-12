package io.github.thebusybiscuit.slimefun4.core.services.sounds;

import lombok.Getter;

/**
 * This structure class holds configured values for a {@link SoundEffect}.
 *
 * @author TheBusyBiscuit
 *
 * @see SoundService
 * @see SoundEffect
 *
 */
public class SoundConfiguration {
    private final String sound;
    @Getter
    private final float volume;
    @Getter
    private final float pitch;

    protected SoundConfiguration(String sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public String getSoundId() {
        return sound;
    }

}
