package org.example;// FixedListRandomStream.java
import java.util.List;

public class FixedListRandomStream implements RandomStream {
    private final List<Double> data;
    private int idx = 0;

    public FixedListRandomStream(List<Double> data) {
        this.data = data;
    }

    @Override
    public double next() {
        if (!hasNext()) {
            throw new RuntimeException("Todos os rndnumbers foram consumidos!");
        }
        return data.get(idx++);
    }

    @Override
    public boolean hasNext() {
        return idx < data.size();
    }
}
