package io.halkyon;

import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain(name="client")
public class ClientApplication {
    public static void main(String[] args) {
        SharedApplication.main(ClientCommands.class, args, (remainingArgs) -> {
            if (remainingArgs.size() > 0) {
                System.out.println("Remaining args are: ");
                remainingArgs.stream().forEach(a -> {
                    System.out.println("\t" + a);
                });
            }
            return null;
        });
    }
}