package de.twometer.orion.res.cache;

import de.twometer.orion.gl.Texture;
import de.twometer.orion.res.TextureLoader;
import de.twometer.orion.util.Cache;

import java.util.HashMap;
import java.util.Map;

public class TextureProvider extends Cache<String, Texture> {

    @Override
    protected Texture create(String s) {
        return TextureLoader.loadTexture(s);
    }

}
