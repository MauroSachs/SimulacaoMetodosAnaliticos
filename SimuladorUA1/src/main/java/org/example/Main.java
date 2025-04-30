package org.example;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.err.println("Uso: java -jar simulador.jar <arquivo-yaml>");
            System.exit(1);
        }

        Yaml yaml = new Yaml();
        try (InputStream in = Files.newInputStream(Path.of(args[0]))) {
            Config cfg = yaml.loadAs(in, Config.class);

            RandomStream rnd =
                    (cfg.seeds != null && !cfg.seeds.isEmpty())
                            ? new MultiSeedRandomStream(cfg.seeds, cfg.rndnumbersPerSeed)
                            : (cfg.rndnumbers != null)
                            ? new FixedListRandomStream(cfg.rndnumbers)
                            : new DefaultRandomStream();

            new Simulation(cfg, rnd).run();
        }
    }
}
