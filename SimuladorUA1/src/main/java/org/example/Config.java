package org.example;

import java.util.List;
import java.util.Map;

public class Config {
    public Map<String, Double> arrivals;
    public Map<String, QueueConfig> queues;
    public List<RoutingConfig> network;
    public List<Double> rndnumbers;
    public int rndnumbersPerSeed;
    public List<Long> seeds;
}
