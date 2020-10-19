package de.twometer.orion.res.cache;

import de.twometer.orion.gl.Texture;
import de.twometer.orion.res.TextureLoader;

import java.util.HashMap;
import java.util.Map;

public class TextureProvider {

    private final Map<String, Texture> cache = new HashMap<>();

    public Texture getTexture(String name) {
        Texture tex = cache.get(name);
        if (tex == null) {
            tex = TextureLoader.loadTexture(name);
            cache.put(name, tex);
        }
        return tex;
    }

}
