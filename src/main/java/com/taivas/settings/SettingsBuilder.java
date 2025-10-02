package com.taivas.settings;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsBuilder {

    private final String path;
    private final Settings settingsToRebuild;
    private final Map<String, String> properties;

    public SettingsBuilder(String path) {
        this.path = path;
        this.settingsToRebuild = null;
        this.properties = new HashMap<>();
    }

    protected SettingsBuilder(Settings settingsToRebuild) {
        this.path = null;
        this.settingsToRebuild = settingsToRebuild;
        this.properties = new HashMap<>();
    }

    /**
     *
     * Register a default property if it doesn't already exist
     *
     * @param key property key
     * @param defaultValue property default value
     */
    public SettingsBuilder withDefault(String key, String defaultValue) {
        properties.put(key, defaultValue);
        return this;
    }

    /**
     *
     * Finalize registering properties and commit it to the settings and the file
     *
     * @throws IOException if it can't create the file
     */
    public Settings build() throws IOException {
        Settings settings;
        if (settingsToRebuild == null) {
            settings = new Settings(path);
            settings.load();
        } else {
            settings = settingsToRebuild;
        }

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (settings.hasProperty(entry.getKey())) {
                continue;
            }

            settings.set(entry.getKey(), entry.getValue());
        }

        settings.store();
        return settings;
    }
}
