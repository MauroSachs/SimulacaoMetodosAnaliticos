package org.example;

public interface RandomStream {
    double next();

    default boolean hasNext() {
        return true;
    }
}
