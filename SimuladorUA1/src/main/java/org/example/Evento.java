package org.example;

public record Evento(double tempo, Tipo tipo, String queueName) implements Comparable<Evento> {
    public enum Tipo { CHEGADA_EXT, CHEGADA_INT, SAIDA }

    @Override
    public int compareTo(Evento o) {
        return Double.compare(this.tempo, o.tempo);
    }
}