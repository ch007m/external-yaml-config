package io.halkyon.configsource;

import io.smallrye.config.ConfigSourceContext;
import io.smallrye.config.ConfigSourceFactory;
import io.smallrye.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class URLConfigSourceFactory extends YamlUtils implements ConfigSourceFactory {

    @Override
    public Iterable<ConfigSource> getConfigSources(ConfigSourceContext context) {
        final ConfigValue value = context.getValue("config.url");
        if (value != null) {
            return singletonList(new YamlConfigSource(value.getValue()));
        }
        return emptyList();
    }

    public static class YamlConfigSource implements ConfigSource {
        private final Map<String, String> values = new HashMap<>();

        public YamlConfigSource(String configUrl) {
            fetchYaml(configUrl);
        }

        private void fetchYaml(String configUrl) {
            InputStream is = null;
            if (configUrl != null) {
                try {
                    is = Files.newInputStream(Path.of(configUrl));
                    Map<String, String> yamlEntries = streamToMap(is);

                    for (Map.Entry<String, String> entry : yamlEntries.entrySet()) {
                        values.put(entry.getKey(), entry.getValue());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public Set<String> getPropertyNames() {
            return values.keySet();
        }

        @Override
        public String getValue(final String propertyName) {
            return values.get(propertyName);
        }

        @Override
        public String getName() {
            return "database";
        }

        @Override
        public int getOrdinal() {
            return 150;
        }
    }
}
