package io.halkyon;

import io.smallrye.config.SmallRyeConfig;
import io.smallrye.config.SmallRyeConfigBuilder;
import io.smallrye.config.source.yaml.YamlLocationConfigSourceFactory;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import static io.smallrye.config.SmallRyeConfig.SMALLRYE_CONFIG_LOCATIONS;

@Command(name = "mycli-app", mixinStandardHelpOptions = true, version = "mycli-app 1.0")
public class ClientCommands implements Callable {

    ClientConfig cfg;

    // This argument/parameter is processed outside Picocli and using Quarkus
    @CommandLine.Option(names = {"-c", "--config-file"}, description = "Path to a YAML configuration file.", paramLabel = "FILE")
    File configfile;

    @Override
    public Integer call() throws Exception {
        if (configfile != null) {

            System.out.println("Yaml config file: " + configfile.getAbsolutePath());

            System.setProperty(SMALLRYE_CONFIG_LOCATIONS, configfile.getAbsolutePath());

            SmallRyeConfig config = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);
            SmallRyeConfig ClientConfig = new SmallRyeConfigBuilder()
                .withMapping(ClientConfig.class)
                .withSources(new YamlLocationConfigSourceFactory() {
                    @Override
                    protected ConfigSource loadConfigSource(URL url, int ordinal) throws IOException {
                        return super.loadConfigSource(url, 500);
                    }
                })
                .withSources(new ConfigSource() {
                    @Override
                    public Set<String> getPropertyNames() {
                        Set<String> properties = new HashSet<>();
                        config.getPropertyNames().forEach(properties::add);
                        return properties;
                    }

                    @Override
                    public String getValue(final String propertyName) {
                        return config.getRawValue(propertyName);
                    }

                    @Override
                    public String getName() {
                        return "Client Config";
                    }
                })
                .build();

            cfg = ClientConfig.getConfigMapping(ClientConfig.class);
            System.out.println("Kube version: " + cfg.kubernetesVersion().get());
            System.out.println("Name: " + cfg.name());
            System.out.println("Provider: " + cfg.providerId());
            System.out.println("Labels: " + cfg.labels().get());
        }
        return 0;
    }
}