public record Evento(double tempo, Evento.Tipo tipo, int filaIndex) implements Comparable<Evento> {
    public enum Tipo {CHEGADA, SAIDA}

    @Override
    public int compareTo(Evento outro) {
        return Double.compare(this.tempo, outro.tempo);
    }
}