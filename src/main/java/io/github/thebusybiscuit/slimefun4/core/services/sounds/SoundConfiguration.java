package io.github.thebusybiscuit.slimefun4.core.services.sounds;


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
    private final float volume;
    private final float pitch;

    protected SoundConfiguration(String sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public String getSoundId() {
        return sound;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }
}
