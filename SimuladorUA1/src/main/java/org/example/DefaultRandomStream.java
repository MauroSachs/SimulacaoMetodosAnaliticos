package org.example;

import java.util.concurrent.ThreadLocalRandom;

public class DefaultRandomStream implements RandomStream {
    public double next() { return ThreadLocalRandom.current().nextDouble(); }
}
