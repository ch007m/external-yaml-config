package io.halkyon;

import jakarta.enterprise.context.ApplicationScoped;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.concurrent.Callable;

@ApplicationScoped
@Command(name = "mycli-app", mixinStandardHelpOptions = true, version = "mycli-app 1.0")
public class CliAppRunner implements Callable<Integer> {

    @Option(names = {"-c", "--config-file"}, description = "Path to an external YAML configuration file.", paramLabel = "FILE")
    File externalConfigFile;

    public static void main(String... args) {
        int exitCode = new CommandLine(new CliAppRunner()).execute(args);
        System.exit(exitCode);
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
        System.out.println("TODO");

        return 0;
    }
}