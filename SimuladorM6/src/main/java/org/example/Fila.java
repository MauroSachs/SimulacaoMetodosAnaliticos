public class Fila {
    private final int systemCapacity;
    private final int numServers;
    private final double[] serverDepartureTime;
    private int queueSize;
    private int lostCustomers;
    private final double[] stateTime;
    private double lastEventTime;
    private final double minServiceTime;
    private final double maxServiceTime;

    public Fila(int systemCapacity, int numServers, double minServiceTime, double maxServiceTime) {
        this.systemCapacity = systemCapacity;
        this.numServers = numServers;
        this.serverDepartureTime = new double[numServers];
        for (int i = 0; i < numServers; i++) {
            serverDepartureTime[i] = Double.POSITIVE_INFINITY;
        }
        this.queueSize = 0;
        this.lostCustomers = 0;
        this.stateTime = new double[systemCapacity + 1];
        this.lastEventTime = 0.0;
        this.minServiceTime = minServiceTime;
        this.maxServiceTime = maxServiceTime;
    }

    public int getOccupancy() {
        int busyServers = 0;
        for (int i = 0; i < numServers; i++) {
            if (serverDepartureTime[i] < Double.POSITIVE_INFINITY) {
                busyServers++;
            }
        }
        return busyServers + queueSize;
    }

    public double getNextDepartureTime() {
        double nextDepartureTime = Double.POSITIVE_INFINITY;
        for (int i = 0; i < numServers; i++) {
            if (serverDepartureTime[i] < nextDepartureTime) {
                nextDepartureTime = serverDepartureTime[i];
            }
        }
        return nextDepartureTime;
    }

    private void updateStateTime(double currentTime) {
        int occupancy = getOccupancy();
        int state = Math.min(occupancy, systemCapacity);
        stateTime[state] += (currentTime - lastEventTime);
        lastEventTime = currentTime;
    }

    public double processArrival(double currentTime) {
        updateStateTime(currentTime);
        int occupancy = getOccupancy();
        if (occupancy < systemCapacity) {
            for (int i = 0; i < numServers; i++) {
                if (serverDepartureTime[i] == Double.POSITIVE_INFINITY) {
                    double departureTime = currentTime + getServiceTime();
                    serverDepartureTime[i] = departureTime;
                    return departureTime;
                }
            }
            queueSize++;
            return -1;
        } else {
            lostCustomers++;
            return -2;
        }
    }

    public double processDeparture(double currentTime) {
        updateStateTime(currentTime);
        int departingServer = -1;
        double nextDepartureTime = Double.POSITIVE_INFINITY;
        for (int i = 0; i < numServers; i++) {
            if (serverDepartureTime[i] < nextDepartureTime) {
                nextDepartureTime = serverDepartureTime[i];
                departingServer = i;
            }
        }
        if (departingServer != -1) {
            if (queueSize > 0) {
                double newDeparture = currentTime + getServiceTime();
                serverDepartureTime[departingServer] = newDeparture;
                queueSize--;
                return newDeparture;
            } else {
                serverDepartureTime[departingServer] = Double.POSITIVE_INFINITY;
            }
        }
        return Double.POSITIVE_INFINITY;
    }

    public void printStatistics(double totalTime, String filaNome) {
        System.out.println("\n--- Estatísticas da " + filaNome + " ---");
        System.out.printf("Tempo total: %.4f%n", totalTime);
        System.out.println("Estado (número de clientes) | Tempo Acumulado | Probabilidade (Tempo/Total)");
        for (int i = 0; i < systemCapacity; i++) {
            System.out.printf("          %2d               |     %.4f     |     %.4f%n",
                    i, stateTime[i], stateTime[i] / totalTime);
        }
        System.out.printf("         >= %2d              |     %.4f     |     %.4f%n",
                systemCapacity, stateTime[systemCapacity], stateTime[systemCapacity] / totalTime);
        System.out.printf("Clientes rejeitados: %d%n", lostCustomers);
    }

    private double getServiceTime() {
        return Main.getUniform(minServiceTime, maxServiceTime);
    }

}