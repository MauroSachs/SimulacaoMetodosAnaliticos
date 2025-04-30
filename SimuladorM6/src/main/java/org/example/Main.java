import java.util.Scanner;

public class Main {
    private static final long a = 1664525;
    private static final long c = 1013904223;
    private static final long M = (long) Math.pow(2, 32);
    private static long seed = 123456789;

    private static double minInterArrivalTime;
    private static double maxInterArrivalTime;

    public static double nextRandom() {
        seed = (a * seed + c) % M;
        return (double) seed / M;
    }

    public static double getUniform(double min, double max) {
        return min + (max - min) * nextRandom();
    }

    public static double getInterArrivalTime() {
        return getUniform(minInterArrivalTime, maxInterArrivalTime);
    }

    public static void main(String[] args) {
        // Exemplo: 100000 eventos, capacidade 5, 2 servidores por fila, 2 filas em tandem.
        int numExternalArrivals;
        int systemCapacityQueue1;
        int numServersQueue1;
        int systemCapacityQueue2;
        int numServersQueue2;
        double minServiceTimeQueue1;
        double maxServiceTimeQueue1;
        double minServiceTimeQueue2;
        double maxServiceTimeQueue2;
        double firstCustomerArrivalTime = getInterArrivalTime();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite o número de chegadas externas:");
        numExternalArrivals = scanner.nextInt();

        System.out.println("Digite o hora de chegada do primeiro cliente:");
        firstCustomerArrivalTime = scanner.nextDouble();

        System.out.println("Digite a capacidade do sistema da fila 1 (k):");
        systemCapacityQueue1 = scanner.nextInt();

        System.out.println("Digite o número de servidores da fila 1:");
        numServersQueue1 = scanner.nextInt();

        System.out.println("Digite a capacidade do sistema da fila 2 (k):");
        systemCapacityQueue2 = scanner.nextInt();

        System.out.println("Digite o número de servidores da fila 2:");
        numServersQueue2 = scanner.nextInt();

        System.out.println("Digite o tempo mínimo entre chegadas:");
        minInterArrivalTime = scanner.nextDouble();

        System.out.println("Digite o tempo máximo entre chegadas:");
        maxInterArrivalTime = scanner.nextDouble();

        System.out.println("Digite o tempo mínimo de atendimento da fila 1:");
        minServiceTimeQueue1 = scanner.nextDouble();

        System.out.println("Digite o tempo máximo de atendimento da fila 1:");
        maxServiceTimeQueue1 = scanner.nextDouble();

        System.out.println("Digite o tempo mínimo de atendimento da fila 2:");
        minServiceTimeQueue2 = scanner.nextDouble();

        System.out.println("Digite o tempo máximo de atendimento da fila 2:");
        maxServiceTimeQueue2 = scanner.nextDouble();

        Simulation simulation = new Simulation(numExternalArrivals, systemCapacityQueue1, numServersQueue1, systemCapacityQueue2, numServersQueue2,
                minServiceTimeQueue1, maxServiceTimeQueue1, minServiceTimeQueue2, maxServiceTimeQueue2, firstCustomerArrivalTime);
        simulation.run();
    }
}
