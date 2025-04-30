package org.example;

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
    private final RandomStream rnd;

    public Fila(Integer systemCapacity,
                int numServers,
                double minServiceTime,
                double maxServiceTime,
                RandomStream rnd) {
        this.systemCapacity = (systemCapacity == null || systemCapacity <= 0)
                ? Integer.MAX_VALUE
                : systemCapacity;

        this.numServers = numServers;
        this.minServiceTime = minServiceTime;
        this.maxServiceTime = maxServiceTime;
        this.rnd = rnd;

        this.serverDepartureTime = new double[numServers];
        for (int i = 0; i < numServers; i++) {
            serverDepartureTime[i] = Double.POSITIVE_INFINITY;
        }

        this.queueSize = 0;
        this.lostCustomers = 0;
        this.lastEventTime = 0.0;

        int tracked = (this.systemCapacity == Integer.MAX_VALUE) ? 20 : this.systemCapacity;
        this.stateTime = new double[tracked + 1];
    }

    public int getOccupancy() {
        int busy = 0;
        for (double t : serverDepartureTime) {
            if (t < Double.POSITIVE_INFINITY) busy++;
        }
        return busy + queueSize;
    }

    public double getNextDepartureTime() {
        double next = Double.POSITIVE_INFINITY;
        for (double t : serverDepartureTime) {
            if (t < next) next = t;
        }
        return next;
    }

    private void updateStateTime(double now) {
        int occ = getOccupancy();
        int tracked = stateTime.length - 1;
        int state = Math.min(occ, tracked);
        stateTime[state] += now - lastEventTime;
        lastEventTime = now;
    }

    public double processArrival(double currentTime) {
        updateStateTime(currentTime);
        int occ = getOccupancy();

        if (occ < systemCapacity) {
            for (int i = 0; i < numServers; i++) {
                if (serverDepartureTime[i] == Double.POSITIVE_INFINITY) {
                    double dep = currentTime + sampleServiceTime();
                    serverDepartureTime[i] = dep;
                    return dep;
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
        int departing = -1;
        double nextDep = Double.POSITIVE_INFINITY;

        for (int i = 0; i < numServers; i++) {
            if (serverDepartureTime[i] < nextDep) {
                nextDep = serverDepartureTime[i];
                departing = i;
            }
        }

        if (departing >= 0) {
            if (queueSize > 0) {
                queueSize--;
                double dep = currentTime + sampleServiceTime();
                serverDepartureTime[departing] = dep;
                return dep;
            } else {
                serverDepartureTime[departing] = Double.POSITIVE_INFINITY;
            }
        }
        return Double.POSITIVE_INFINITY;
    }

    private double sampleServiceTime() {
        return minServiceTime + (maxServiceTime - minServiceTime) * rnd.next();
    }

    public void printStatistics(double totalTime, String name) {
        System.out.println("\n--- Estatísticas da " + name + " ---");
        System.out.printf("Tempo total: %.4f%n", totalTime);
        System.out.println("Estado | Tempo Acumulado | Prob. (tempo/total)");

        int tracked = stateTime.length - 1;
        for (int i = 0; i < tracked; i++) {
            System.out.printf("   %2d   |  %10.4f    |   %6.4f%n",
                    i, stateTime[i], stateTime[i] / totalTime);
        }

        System.out.printf("  >=%2d  |  %10.4f    |   %6.4f%n",
                tracked, stateTime[tracked], stateTime[tracked] / totalTime);
        System.out.printf("Clientes rejeitados: %d%n", lostCustomers);
    }
}
