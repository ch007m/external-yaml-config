package io.halkyon;

import io.quarkus.runtime.QuarkusApplication;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MyQuarkusApplication implements QuarkusApplication {

    @Inject
    ClientConfig cfg;

    @Override
    public int run(String... args) throws Exception {
        return 0;
    }
}
