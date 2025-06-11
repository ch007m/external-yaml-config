package io.halkyon;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import java.util.concurrent.Callable;

//@QuarkusMain
@Command(name = "mycli-app", mixinStandardHelpOptions = true, version = "mycli-app 1.0")
public class ClientCommands implements Callable {

    @Inject
    ClientConfig cfg;

    // This argument/parameter is processed outside Picocli and using Quarkus
    /*@Option(names = {"-c", "--config-file"}, description = "Path to a YAML configuration file.", paramLabel = "FILE")
    File externalConfigFile;
    */

    @Override
    public Integer call() throws Exception {
        System.out.println("!! Client Config content is: ");
        System.out.println("Kube version: " + cfg.kubernetesVersion().get());
        System.out.println("Name: " + cfg.name());
        System.out.println("Provider: " + cfg.providerId());
        System.out.println("Labels: " + cfg.labels().get());
        return 0;
    }
}