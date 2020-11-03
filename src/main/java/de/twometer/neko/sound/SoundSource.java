package de.twometer.neko.sound;

import de.twometer.neko.core.NekoApp;
import de.twometer.neko.util.Log;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.openal.AL10.*;

public class SoundSource {

    private final int sourceId;

    private static final List<Integer> soundSources = new ArrayList<>();

    SoundSource(int bufferId) {
        this.sourceId = newSoundSource();
        pause();
        alSourcei(sourceId, AL_BUFFER, bufferId);

        // Reset source
        alSourcei(sourceId, AL_SOURCE_ABSOLUTE, AL_FALSE);
        alSourcei(sourceId, AL_SOURCE_RELATIVE, AL_FALSE);
        alSourcei(sourceId, AL_LOOPING, AL_FALSE);
        alSourcef(sourceId, AL_GAIN, 1.0f);
        alSourcef(sourceId, AL_PITCH, 1.0f);
        alSourcef(sourceId, AL_ROLLOFF_FACTOR, 1.0f);
        alSource3f(sourceId, AL_POSITION, 0, 0, 0);
    }

    public SoundSource setAbsolute(boolean absolute) {
        if (absolute) {
            alSourcei(sourceId, AL_SOURCE_ABSOLUTE, AL_TRUE);
            alSourcei(sourceId, AL_SOURCE_RELATIVE, AL_FALSE);
        } else {
            alSourcei(sourceId, AL_SOURCE_ABSOLUTE, AL_FALSE);
            alSourcei(sourceId, AL_SOURCE_RELATIVE, AL_TRUE);
        }
        return this;
    }

    public SoundSource setLooping(boolean looping) {
        alSourcei(sourceId, AL_LOOPING, looping ? AL_TRUE : AL_FALSE);
        return this;
    }

    public SoundSource setGain(float gain) {
        alSourcef(sourceId, AL_GAIN, gain);
        return this;
    }

    public SoundSource setRolloffFactor(float d) {
        alSourcef(sourceId, AL_ROLLOFF_FACTOR, d);
        return this;
    }

    public SoundSource setSpeed(Vector3f speed) {
        alSource3f(sourceId, AL_VELOCITY, speed.x, speed.y, speed.z);
        return this;
    }

    public SoundSource setPosition(Vector3f position) {
        alSource3f(sourceId, AL_POSITION, position.x, position.y, position.z);
        return this;
    }

    public SoundSource setPitch(float pitch) {
        alSourcef(sourceId, AL_PITCH, pitch);
        return this;
    }

    public SoundSource setProperty(int param, float value) {
        alSourcef(sourceId, param, value);
        return this;
    }

    public SoundSource play() {
        alSourcePlay(sourceId);
        return this;
    }

    public boolean isPlaying() {
        return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING;
    }

    public void pause() {
        alSourcePause(sourceId);
    }

    public void stop() {
        alSourceStop(sourceId);
    }

    public void destroy() {
        stop();
        alDeleteSources(sourceId);
    }

    private static int newSoundSource() {
        for (var src : soundSources) {
            if (alGetSourcei(src, AL_SOURCE_STATE) == AL_STOPPED)
                return src;
        }
        Log.d("Generating new source...");

        // We have to create a new one
        if (soundSources.size() >= NekoApp.get().getSoundFX().getOpenAL().getMaxSources()) {
            throw new IllegalStateException("Maximum number of concurrent sound sources exceeded.");
        }
        var src = alGenSources();
        soundSources.add(src);
        return src;
    }

}
