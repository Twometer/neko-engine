package de.twometer.neko.gui;

import de.twometer.neko.res.StringLoader;
import de.twometer.neko.util.Log;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

public class I18n {

    private final String currentLocale = Locale.getDefault().toLanguageTag();

    private String fallbackLocale = "en-US";

    private Properties currentStrings;
    private Properties fallbackStrings;

    public void load() {
        Log.d("Current locale: " + currentLocale);
        Log.d("Fallback locale: " + fallbackLocale);

        try {
            currentStrings = StringLoader.loadStrings(currentLocale + ".properties");
            fallbackStrings = StringLoader.loadStrings(fallbackLocale + ".properties");
        } catch (IOException e) {
            Log.w("No language files loaded: " + e.toString());
            currentStrings = new Properties();
            fallbackStrings = new Properties();
        }
    }

    public String resolve(String str) {
        var resultBuilder = new StringBuilder();
        var keyBuilder = new StringBuilder();
        var input = str.toCharArray();
        for (int i = 0; i < input.length; i++) {
            if (str.charAt(i) == '{') {
                String key = null;
                for (int j = i + 1; j < input.length; j++)
                    if (str.charAt(j) != '}') {
                        keyBuilder.append(str.charAt(j));
                    } else {
                        key = keyBuilder.toString();
                        keyBuilder.setLength(0);
                        break;
                    }

                if (key == null)
                    throw new RuntimeException("Malformed i18n string! Missing '}'");
                i += key.length() + 1;
                resultBuilder.append(resolveKey(key));
            } else {
                resultBuilder.append(str.charAt(i));
            }
        }

        return resultBuilder.toString();
    }

    private String resolveKey(String key) {
        var current = currentStrings.getProperty(key, null);
        if (current == null) {
            var fallback = fallbackStrings.getProperty(key, null);
            if (fallback == null)
                return key;
            else
                return fallback;
        } else return current;
    }

    public void setFallbackLocale(String fallbackLocale) {
        this.fallbackLocale = fallbackLocale;
        load();
    }

    public String getCurrentLocale() {
        return currentLocale;
    }

}
