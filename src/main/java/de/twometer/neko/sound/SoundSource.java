package de.twometer.neko.sound;

import org.joml.Vector3f;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.AL10.alDeleteSources;

public class SoundSource {

    private final int sourceId;

    SoundSource(int bufferId) {
        this.sourceId = alGenSources();
        stop();
        alSourcei(sourceId, AL_BUFFER, bufferId);
    }

    public SoundSource setAbsolute(boolean absolute) {
        if (absolute)
            alSourcei(sourceId, AL_SOURCE_ABSOLUTE, AL_TRUE);
        else
            alSourcei(sourceId, AL_SOURCE_RELATIVE, AL_TRUE);
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

}
