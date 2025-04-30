package org.example;

import java.util.List;

public class MultiSeedRandomStream implements RandomStream {
    private final long[] seeds;
    private static final long a = 1664525;
    private static final long c = 1013904223;
    private final long M = (long) Math.pow(2, 32);

    private final int blockSize;
    private final int totalNumbers;

    private int currentSeed = 0;
    private int usedInBlock = 0;
    private int generatedCount = 0;

    public MultiSeedRandomStream(List<Long> seeds, int blockSize) {
        this.seeds = seeds.stream().mapToLong(Long::longValue).toArray();
        this.blockSize = blockSize;
        this.totalNumbers = blockSize * seeds.size();
    }

    @Override
    public double next() {
        if (!hasNext()) {
            throw new RuntimeException("Todos os números de todas as seeds já foram consumidos.");
        }

        seeds[currentSeed] = (a * seeds[currentSeed] + c) % M;
        double value = (double) seeds[currentSeed] / M;

        usedInBlock++;
        generatedCount++;

        if (usedInBlock >= blockSize) {
            usedInBlock = 0;
            currentSeed++;
        }

        return value;
    }

    @Override
    public boolean hasNext() {
        return generatedCount < totalNumbers;
    }
}
