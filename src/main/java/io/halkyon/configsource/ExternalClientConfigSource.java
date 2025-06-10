package io.halkyon.configsource;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * A config source to parse external YAML Config file
 * Parse the config file using the parameter: -c" or "--config-file"
 * Will process any argument from the parsed file as key/value pairs
 */
public class ExternalClientConfigSource extends YamlUtils implements ConfigSource {

    public static List<String> setupConfigSource(String[] args) {
        List<String> remainingArgs = new ArrayList<>();
        String configFilePath = null;

        for (int i = 0; i < args.length; i++) {
            String current = args[i];
            if (current.startsWith("-c") || current.startsWith("--config-file")) {
                // Check if the arg string includes *.yml or *.yaml
                if (i + 1 < args.length) {
                    configFilePath = args[i + 1];
                    if (configFilePath.endsWith(".yml") || configFilePath.endsWith(".yaml")) {
                        try {
                            InputStream is = Files.newInputStream(Path.of(configFilePath));
                            Map<String, String> yamlEntries = streamToMap(is);

                            for (Map.Entry<String, String> entry : yamlEntries.entrySet()) {
                                System.out.println(entry);
                                put(entry.getKey(), entry.getValue());
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        // TODO: Handle file which are not YAML
                    }
                } else {
                    // TODO:Handle exception here
                }
            } else {
                remainingArgs.add(args[i]);
            }
        }
        return remainingArgs;
    }

    private static final Map<String, String> configuration = new HashMap<>();

    public static void put(String key, String value) {
        configuration.put(key, value);
    }

    @Override
    public int getOrdinal() {
        return 275;
    }

    @Override
    public Set<String> getPropertyNames() {
        return configuration.keySet();
    }

    @Override
    public String getValue(final String propertyName) {
        return configuration.get(propertyName);
    }

    @Override
    public String getName() {
        return ExternalClientConfigSource.class.getSimpleName();
    }
}
