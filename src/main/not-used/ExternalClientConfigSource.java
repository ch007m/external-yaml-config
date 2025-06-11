import io.halkyon.configsource.YamlUtils;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.config.spi.ConfigSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * A config source to parse external YAML Config file
 * Parse the config file using the parameter: -c or --config-file
 */
@RegisterForReflection
public class ExternalClientConfigSource extends YamlUtils implements ConfigSource {
    private static String configFilePath = null;

    public static void setupConfigSourceFromUrl(String url) {
        processConfigFile(url);
    }
    public static void setupConfigSourceFromArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String current = args[i];
            if (current.startsWith("-c") || current.startsWith("--config-file")) {
                if (i + 1 < args.length) {
                    processConfigFile(args[i + 1]);
                } else {
                    // TODO: Add code to throw an exception when no file path
                }
            }
        }
    }

    private static void processConfigFile(String configFilePath) {
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
    }

    public static final Map<String, String> configuration = new HashMap<>();

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
