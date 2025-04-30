package org.example;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Simulation {
    private final PriorityQueue<Evento> eventQueue = new PriorityQueue<>();
    private final Map<String, Fila> filas;
    private final Map<String, QueueConfig> queueConfigs;
    private final Map<String, List<Route>> routing;
    private final RandomStream rnd;
    private double simulationTime = 0;

    public Simulation(Config cfg, RandomStream rnd) {
        this.rnd = rnd;
        this.queueConfigs = Map.copyOf(cfg.queues);
        this.filas = new java.util.HashMap<>();

        for (Map.Entry<String, QueueConfig> entry : cfg.queues.entrySet()) {
            String name = entry.getKey();
            QueueConfig qc = entry.getValue();
            filas.put(name, new Fila(qc.capacity, qc.servers, qc.minService, qc.maxService, rnd));
        }

        this.routing = new java.util.HashMap<>();
        for (RoutingConfig rc : cfg.network) {
            routing.computeIfAbsent(rc.source, k -> new java.util.ArrayList<>())
                    .add(new Route(rc.target, rc.probability));
        }

        for (Map.Entry<String, Double> arrival : cfg.arrivals.entrySet()) {
            eventQueue.add(new Evento(arrival.getValue(), Evento.Tipo.CHEGADA_EXT, arrival.getKey()));
        }
    }

    public void run() {
        System.out.println("=== Início da Simulação ===");

        while (!eventQueue.isEmpty() && rnd.hasNext()) {
            Evento evt = eventQueue.poll();
            simulationTime = evt.tempo();
            String qname = evt.queueName();
            Fila fila = filas.get(qname);

            if (evt.tipo() == Evento.Tipo.CHEGADA_EXT || evt.tipo() == Evento.Tipo.CHEGADA_INT) {
                double dep = fila.processArrival(simulationTime);
                if (dep > 0) {
                    eventQueue.add(new Evento(dep, Evento.Tipo.SAIDA, qname));
                }

                QueueConfig qc = queueConfigs.get(qname);
                if (evt.tipo() == Evento.Tipo.CHEGADA_EXT &&
                        qc.minArrival != null && qc.maxArrival != null && rnd.hasNext()) {
                    double ia = qc.minArrival + rnd.next() * (qc.maxArrival - qc.minArrival);
                    double nextArr = simulationTime + ia;
                    eventQueue.add(new Evento(nextArr, Evento.Tipo.CHEGADA_EXT, qname));
                }

            } else {
                double nextDep = fila.processDeparture(simulationTime);
                if (!Double.isInfinite(nextDep)) {
                    eventQueue.add(new Evento(nextDep, Evento.Tipo.SAIDA, qname));
                }

                List<Route> routes = routing.get(qname);
                if (routes != null && !routes.isEmpty() && rnd.hasNext()) {
                    double u = rnd.next();
                    double cum = 0.0;
                    for (Route r : routes) {
                        cum += r.probability();
                        if (u < cum) {
                            eventQueue.add(new Evento(simulationTime, Evento.Tipo.CHEGADA_INT, r.targetQueue()));
                            break;
                        }
                    }
                }
            }
        }

        if (!rnd.hasNext()) {
            System.out.println("\n*** Simulação terminou: todos os números aleatórios foram consumidos. ***");
        } else {
            System.out.println("\n*** Simulação terminou: fila de eventos esvaziou. ***");
        }

        System.out.printf("=== Estatísticas finais (t=%.4f) ===%n", simulationTime);
        filas.forEach((name, f) -> f.printStatistics(simulationTime, name));
    }
}
