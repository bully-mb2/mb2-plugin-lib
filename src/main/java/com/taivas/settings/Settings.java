package com.taivas.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;

public class Settings {

    private static final Logger LOG = LoggerFactory.getLogger(Settings.class);

    private final String filePath;
    private final Map<String, String> properties;

    protected Settings(String filePath) {
        this.filePath = filePath;
        this.properties = new HashMap<>();
    }

    public SettingsBuilder rebuild() {
        return new SettingsBuilder(this);
    }

    /**
     *
     * Loads a properties file relative to the executable.
     * If no properties file can be found it will try to create a blank
     * ready for writing to
     *
     * @throws IOException if the template file could not be read
     */
    public void load() throws IOException {
        LOG.info("Loading config");
        File file = new File(filePath);
        if (file.createNewFile()) {
            LOG.info("No config file found, creating new one and aborting load");
            return;
        }

        Map<String, String> loadedProperties = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            for (String line; (line = reader.readLine()) != null; ) {
                String[] split = line.split("=");
                if (split.length < 2) {
                    continue;
                }

                loadedProperties.put(split[0], split[1]);
            }
        }

        int overwritingCount = 0;
        for (Map.Entry<String, String> entry : loadedProperties.entrySet()) {
            if (properties.containsKey(entry.getKey())) {
                overwritingCount++;
            }

            properties.put(entry.getKey(), entry.getValue());
        }

        LOG.info("Config loaded, {} properties overwritten", overwritingCount);
    }

    /**
     *
     * Stores the properties to a file
     *
     * @throws IOException if it can't create the file
     */
    public void store() throws IOException {
        LOG.info("Storing properties");

        File file = new File(filePath);
        if (file.createNewFile()) {
            LOG.info("No config file found, creating new one");
        }

        Set<Map.Entry<String, String>> entries = properties.entrySet();
        List<String> lines = new ArrayList<>(entries.size());
        for (Map.Entry<String, String> entry : entries) {
            lines.add(String.format("%s=%s\n", entry.getKey(), entry.getValue()));
        }

        lines.sort(Comparator.naturalOrder());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
            }

            writer.flush();
        }
    }

    public boolean hasProperty(String key) {
        return properties.get(key) != null;
    }

    public void set(String key, String value) {
        properties.put(key, value);
    }

    public String get(String key) throws MissingPropertyException {
        String value = properties.get(key);
        if (value == null) {
            throw new MissingPropertyException("No value found for key " + key);
        }

        return value;
    }

    public int getInt(String key) throws MissingPropertyException, InvalidPropertyException {
        String value = get(key);

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new InvalidPropertyException(String.format("Couldn't parse (%s=%s) to int", key, value), e);
        }
    }

    public boolean getBoolean(String key) throws MissingPropertyException, InvalidPropertyException {
        String value = get(key);

        if (value.equals("true")) {
            return true;
        } else if (value.equals("false")) {
            return false;
        }

        throw new InvalidPropertyException(String.format("Couldn't parse (%s=%s) to boolean", key, value));
    }

    public InetSocketAddress getAddress(String key) throws MissingPropertyException, InvalidPropertyException {
        String value = get(key);
        String[] split = value.split(":");

        try {
            return new InetSocketAddress(split[0], Integer.parseInt(split[1]));
        } catch (NumberFormatException e) {
            throw new InvalidPropertyException(String.format("Couldn't make address for (%s=%s) expected (%s=host:port)", key, value, key), e);
        }
    }

    @Override
    public String toString() {
        return "Settings{properties=" + properties + "}";
    }
}
