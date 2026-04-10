package passatempo;

public class Musica {
    private String nome;
    private String artista;
    private String caminhoArquivo;
    private String caminhoCapa;
    private long duracaoMs;

    public Musica(String nome, String artista, String caminhoArquivo, String caminhoCapa) {
        this.nome = nome;
        this.artista = artista;
        this.caminhoArquivo = caminhoArquivo;
        this.caminhoCapa = caminhoCapa;
        this.duracaoMs = 0;
    }

    public String getNome() { return nome; }
    public String getArtista() { return artista; }
    public String getCaminhoArquivo() { return caminhoArquivo; }
    public String getCaminhoCapa() { return caminhoCapa; }
    public long getDuracaoMs() { return duracaoMs; }
    public void setDuracaoMs(long duracaoMs) { this.duracaoMs = duracaoMs; }

    public String getDuracaoFormatada() {
        if (duracaoMs <= 0) return "--:--";
        long segundos = duracaoMs / 1000;
        long min = segundos / 60;
        long seg = segundos % 60;
        return String.format("%02d:%02d", min, seg);
    }

    @Override
    public String toString() {
        return nome + " - " + artista;
    }
}