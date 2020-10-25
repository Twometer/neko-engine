package de.twometer.neko.res.cache;

import de.twometer.neko.gl.Texture;
import de.twometer.neko.res.TextureLoader;
import de.twometer.neko.util.Cache;

public class TextureProvider extends Cache<String, Texture> {

    @Override
    protected Texture create(String s) {
        return TextureLoader.loadTexture(s);
    }

}
