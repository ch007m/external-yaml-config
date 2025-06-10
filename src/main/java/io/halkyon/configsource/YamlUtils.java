package io.halkyon.configsource;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import io.smallrye.common.constraint.Assert;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Tag;

public class YamlUtils {

    public static Map<String, String> streamToMap(InputStream inputStream) throws IOException {
        Assert.checkNotNullParam("inputStream", inputStream);
        final Map<String, String> yamlInput = new TreeMap<>();
        try {
            final Iterable<Object> objects = new Yaml(new StringConstructor(new LoaderOptions())).loadAll(inputStream);
            for (Object object : objects) {
                if (object instanceof Map) {
                    yamlInput.putAll(yamlInputToMap((Map<Object, Object>) object));
                }
            }
            inputStream.close();
        } catch (Throwable t) {
            try {
                inputStream.close();
            } catch (Throwable t2) {
                t.addSuppressed(t2);
            }
            throw t;
        }
        return yamlInput;
    }

    private static Map<String, String> stringToMap(String str) {
        final Map<String, String> yamlInput = new TreeMap<>();
        final Iterable<Object> objects = new Yaml(new StringConstructor(new LoaderOptions())).loadAll(str);
        for (Object object : objects) {
            if (object instanceof Map) {
                yamlInput.putAll(yamlInputToMap((Map<Object, Object>) object));
            }
        }
        return yamlInput;
    }

    private static Map<String, String> yamlInputToMap(final Map<Object, Object> yamlInput) {
        final Map<String, String> properties = new TreeMap<>();
        if (yamlInput != null) {
            flattenYaml("", yamlInput, properties, false);
        }
        return properties;
    }

    private static void flattenYaml(String path, Map<Object, Object> source, Map<String, String> target, boolean indexed) {
        source.forEach((originalKey, value) -> {
            String key;
            if (originalKey == null) {
                key = "";
            } else {
                key = originalKey.toString();
            }

            if (key.contains(".")) {
                key = "\"" + key + "\"";
            }

            if (!key.isEmpty() && path != null && !path.isEmpty()) {
                key = indexed ? path + key : path + "." + key;
            } else if (path != null && !path.isEmpty()) {
                key = path;
            }

            if (value instanceof String) {
                target.put(key, (String) value);
            } else if (value instanceof Map) {
                flattenYaml(key, (Map<Object, Object>) value, target, false);
            } else if (value instanceof List) {
                final List<Object> list = (List<Object>) value;
                flattenList(key, list, target);
                for (int i = 0; i < list.size(); i++) {
                    flattenYaml(key, Collections.singletonMap("[" + i + "]", list.get(i)), target, true);
                }
            } else {
                if (value != null) {
                    target.put(key, value.toString());
                }
            }
        });
    }

    // Do not remove this, because Quarkus old ConfigRoots still rely on comma separated values calling Config#getValue and not Config#getValues.
    private static void flattenList(String key, List<Object> source, Map<String, String> target) {
        boolean mixed = false;
        List<String> flatten = new ArrayList<>();
        for (Object value : source) {
            if (value instanceof String || value instanceof Boolean) {
                flatten.add(value.toString());
            } else if (value != null) {
                mixed = true;
                break;
            }
        }

        if (!mixed) {
            target.put(key, flatten.stream().map(value -> {
                StringBuilder sb = new StringBuilder();
                escapeCommas(sb, value, 1);
                return sb.toString();
            }).collect(Collectors.joining(",")));
        }
    }

    private static void escapeCommas(StringBuilder b, String src, int escapeLevel) {
        int cp;
        for (int i = 0; i < src.length(); i += Character.charCount(cp)) {
            cp = src.codePointAt(i);
            if (cp == '\\' || cp == ',') {
                for (int j = 0; j < escapeLevel; j++) {
                    b.append('\\');
                }
            }
            b.appendCodePoint(cp);
        }
    }


    /**
     * Override some yaml constructors, so that the value written in the flatten result is more alike with the
     * source. For instance, timestamps may be written in a completely different format which prevents converters to
     * convert the correct value.
     */
    private static class StringConstructor extends SafeConstructor {
        public StringConstructor(final LoaderOptions loadingConfig) {
            super(loadingConfig);
            this.yamlConstructors.put(Tag.INT, new ConstructYamlStr());
            this.yamlConstructors.put(Tag.FLOAT, new ConstructYamlStr());
            this.yamlConstructors.put(Tag.TIMESTAMP, new ConstructYamlStr());
        }
    }
}
