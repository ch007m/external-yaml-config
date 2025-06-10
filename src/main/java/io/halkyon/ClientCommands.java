package io.halkyon;

import io.quarkus.runtime.QuarkusApplication;
import io.smallrye.config.SmallRyeConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.ConfigProvider;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.lang.reflect.Field;
import java.util.concurrent.Callable;

@ApplicationScoped
@Command(name = "mycli-app", mixinStandardHelpOptions = true, version = "mycli-app 1.0")
public class ClientCommands implements Callable, QuarkusApplication {

    @Inject
    ClientConfig config;

    @Option(names = {"-c", "--config-file"}, description = "Path to an external YAML configuration file.", paramLabel = "FILE")
    File externalConfigFile;

    @Override
    public int run(String... args) throws Exception {
        System.out.println("Picocli started ...");
        return new CommandLine(ClientCommands.class).execute(args);
    }

    @Override
    public Integer call() throws Exception {
        var props = ConfigProvider.getConfig().getPropertyNames();
        props.forEach(n -> {
            if (n.startsWith("kind")) {
                System.out.println("Source : " + ConfigProvider.getConfig().getConfigValue(n).getSourceName());
                System.out.println("Name: " + n + ", value : " + ConfigProvider.getConfig().getConfigValue(n).getValue());
                System.out.println("\n");
            }
        });

        System.out.println("## Show ClientConfig");
        ClientConfig cfg = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class).getConfigMapping(ClientConfig.class);
        System.out.println("Kubeversion: " + cfg.kubernetesVersion());
        System.out.println("Name: " + cfg.name());

        return 0;
    }
}