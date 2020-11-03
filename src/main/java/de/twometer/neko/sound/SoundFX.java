package de.twometer.neko.sound;

import de.twometer.neko.core.NekoApp;
import de.twometer.neko.util.Cache;
import org.joml.Vector3f;

import java.io.IOException;

public class SoundFX {

    private final OpenAL openAL = new OpenAL();

    private final Cache<String, SoundBuffer> bufferCache = new Cache<>() {
        @Override
        public SoundBuffer create(String s) throws IOException {
            return new SoundBuffer(s);
        }
    };

    public void create() {
        openAL.create();
    }

    public void update() {
        var camera = NekoApp.get().getCamera();
        openAL.setPosition(camera.getInterpolatedPosition(NekoApp.get().getTimer().getPartial()));

        var mat = camera.getViewMatrix();
        var at = new Vector3f();
        mat.positiveZ(at).negate();
        var up = new Vector3f();
        mat.positiveY(up);
        openAL.setOrientation(at, up);
    }

    public SoundSource play3d(String sound, Vector3f pos) {
        return sourceBuilder(sound)
                .setAbsolute(true)
                .setPosition(pos)
                .play();
    }

    public SoundSource play(String sound) {
        return sourceBuilder(sound)
                .setAbsolute(false)
                .setPosition(new Vector3f(0, 0, 0))
                .play();
    }

    public SoundSource addAmbiance(String sound, Vector3f pos) {
        return sourceBuilder(sound)
                .setAbsolute(true)
                .setLooping(true)
                .setPosition(pos)
                .play();
    }

    public SoundSource sourceBuilder(String sound) {
        var buffer = bufferCache.get(sound);
        return new SoundSource(buffer.getBufferId());
    }

    public void destroy() {
        openAL.destroy();
    }

    public OpenAL getOpenAL() {
        return openAL;
    }
}
