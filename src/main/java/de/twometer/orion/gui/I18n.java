package de.twometer.orion.gui;

import de.twometer.orion.res.StringLoader;
import de.twometer.orion.util.Log;

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
        return str;
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