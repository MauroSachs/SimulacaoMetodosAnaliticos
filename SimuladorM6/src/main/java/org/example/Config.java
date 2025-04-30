import java.util.List;
import java.util.Map;

public class Config {
    public Map<String, Double> arrivals;        // arrivals: { Q1: 45.0, … }
    public Map<String, QueueConfig> queues;     // queues: { Q1: { … }, Q2: { … }, … }
    public List<RoutingConfig> network;         // network: [ {source,target,probability}, … ]
    public List<Double> rndnumbers;             // opcional
    public int rndnumbersPerSeed;               // opcional
    public List<Long> seeds;                    // opcional
}
