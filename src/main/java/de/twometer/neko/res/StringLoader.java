package de.twometer.neko.res;

import java.io.IOException;
import java.util.Properties;

public class StringLoader {

    public static Properties loadStrings(String name) throws IOException {
        Properties properties = new Properties();
        properties.load(ResourceLoader.openReader(AssetPaths.STRING_PATH + name));
        return properties;
    }

}
