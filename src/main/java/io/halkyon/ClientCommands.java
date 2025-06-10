package io.halkyon;

import io.quarkus.runtime.QuarkusApplication;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.ConfigProvider;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.concurrent.Callable;

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
        /*
        if (externalConfigFile != null) {
            if (!externalConfigFile.exists() || !externalConfigFile.isFile()) {
                System.err.println("Error: External config file not found or is not a regular file: " + externalConfigFile.getAbsolutePath());
                return 1;
            }
            System.setProperty("quarkus.config.locations", "file:" + externalConfigFile.getAbsolutePath());
            System.out.println("Loading external config from: " + externalConfigFile.getAbsolutePath());
        } else {
            System.out.println("No external config file specified. Using only internal application.yaml.");
        }
        */
        var props = ConfigProvider.getConfig().getPropertyNames();
        props.forEach(n -> {
            if (n.startsWith("kind")) {
                System.out.println("Source : " + ConfigProvider.getConfig().getConfigValue(n).getSourceName());
                System.out.println("Name: " + n + ", value : " + ConfigProvider.getConfig().getConfigValue(n).getValue());
                System.out.println("\n");
            }
        });
        return 0;
    }
}