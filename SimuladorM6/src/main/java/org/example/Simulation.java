import java.util.PriorityQueue;

public class Simulation {
    private int remainingExternalArrivals;
    private double simulationTime;
    private PriorityQueue<Evento> eventQueue;
    private Fila fila1;
    private Fila fila2;
    private final int totalExternalArrivals;

    /**
     * Constrói a simulação.
     * @param externalArrivals Número de chegadas externas (na Fila 1) a serem processadas.
     * @param systemCapacityQueue1 Capacidade do sistema da Fila 1 (k).
     * @param numServersQueue1 Número de servidores da Fila 1.
     * @param systemCapacityQueue2 Capacidade do sistema da Fila 2 (k).
     * @param numServersQueue2 Número de servidores da Fila 2.
     * @param minServiceTimeQueue1 Tempo mínimo de atendimento na Fila 1.
     * @param maxServiceTimeQueue1 Tempo máximo de atendimento na Fila 1.
     * @param minServiceTimeQueue2 Tempo mínimo de atendimento na Fila 2.
     * @param maxServiceTimeQueue2 Tempo máximo de atendimento na Fila 2.
     * @see Fila
     * @see Evento
     */
    public Simulation(int externalArrivals, int systemCapacityQueue1, int numServersQueue1, int systemCapacityQueue2, int numServersQueue2,
                      double minServiceTimeQueue1, double maxServiceTimeQueue1,
                      double minServiceTimeQueue2, double maxServiceTimeQueue2,
                      double firstCustomerArrivalTime) {
        this.totalExternalArrivals = externalArrivals;
        this.remainingExternalArrivals = externalArrivals;
        this.simulationTime = 0.0;
        this.eventQueue = new PriorityQueue<>();
        // Cria as duas filas: a Fila 1 recebe os clientes do exterior
        // e a Fila 2 recebe os clientes encaminhados da Fila 1.
        this.fila1 = new Fila(systemCapacityQueue1, numServersQueue1, minServiceTimeQueue1, maxServiceTimeQueue1);
        this.fila2 = new Fila(systemCapacityQueue2, numServersQueue2, minServiceTimeQueue2, maxServiceTimeQueue2);
        // Agenda a primeira chegada externa na Fila 1.
        eventQueue.add(new Evento(firstCustomerArrivalTime, Evento.Tipo.CHEGADA, 0));
    }

    public void run() {
        while (!eventQueue.isEmpty() && remainingExternalArrivals > 0) {
            Evento evento = eventQueue.poll();
            simulationTime = evento.tempo();

            if (evento.filaIndex() == 0) { // Fila 1
                processFila1(evento);
            } else { // Fila 2
                processFila2(evento);
            }
        }
        printFinalStatistics();
    }

    private void processFila1(Evento evento) {
        if (evento.tipo() == Evento.Tipo.CHEGADA) {
            double departureTime = fila1.processArrival(simulationTime);
            if (departureTime > 0) {
                eventQueue.add(new Evento(departureTime, Evento.Tipo.SAIDA, 0));
            }
            // Agenda próxima chegada externa na Fila 1, se houver demanda.
            remainingExternalArrivals--;
            if (remainingExternalArrivals > 0) {
                double nextArrival = simulationTime + Main.getInterArrivalTime();
                eventQueue.add(new Evento(nextArrival, Evento.Tipo.CHEGADA, 0));
            }
        } else if (evento.tipo() == Evento.Tipo.SAIDA) {
            double newDeparture = fila1.processDeparture(simulationTime);
            if (newDeparture < Double.POSITIVE_INFINITY) {
                eventQueue.add(new Evento(newDeparture, Evento.Tipo.SAIDA, 0));
            }
            // Encaminha o cliente para a Fila 2.
            eventQueue.add(new Evento(simulationTime, Evento.Tipo.CHEGADA, 1));
        }
    }

    private void processFila2(Evento evento) {
        if (evento.tipo() == Evento.Tipo.CHEGADA) {
            double departureTime = fila2.processArrival(simulationTime);
            if (departureTime > 0) {
                eventQueue.add(new Evento(departureTime, Evento.Tipo.SAIDA, 1));
            }
        } else if (evento.tipo() == Evento.Tipo.SAIDA) {
            double newDeparture = fila2.processDeparture(simulationTime);
            if (newDeparture < Double.POSITIVE_INFINITY) {
                eventQueue.add(new Evento(newDeparture, Evento.Tipo.SAIDA, 1));
            }
            // Após o atendimento na Fila 2, o cliente deixa o sistema.
        }
    }

    private void printFinalStatistics() {
        System.out.printf("\n--- Estatísticas da Simulação ---\nTempo total de simulação: %.4f%n", simulationTime);
        fila1.printStatistics(simulationTime, "Fila 1");
        fila2.printStatistics(simulationTime, "Fila 2");
        System.out.printf("Chegadas externas processadas (Fila 1): %d%n", totalExternalArrivals);
    }
}