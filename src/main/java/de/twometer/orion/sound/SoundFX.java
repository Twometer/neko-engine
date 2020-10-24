package de.twometer.orion.sound;

import de.twometer.orion.core.OrionApp;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class SoundFX {

    private final OpenAL openAL = new OpenAL();

    private final Map<String, SoundBuffer> bufferCache = new HashMap<>();

    public void create() {
        openAL.create();
    }

    public void update() {
        var camera = OrionApp.get().getCamera();
        openAL.setPosition(camera.getPosition());

        var mat = camera.getViewMatrix();
        var at = new Vector3f();
        mat.positiveZ(at).negate();
        var up = new Vector3f();
        mat.positiveY(up);
        openAL.setOrientation(at, up);
    }

    public SoundSource newAmbiance(String sound, Vector3f pos) {
        // TODO
        return null;
    }

    public SoundSource play3d(String sound, Vector3f pos) {
        // TODO
        return null;
    }

    public SoundSource play(String sound) {
        // TODO
        return null;
    }

    public void destroy() {
        openAL.destroy();
    }


}
