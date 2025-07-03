package passatempo;

public class Musica {
    private String nome;
    private String artista;
    private String caminhoArquivo;
    private String caminhoCapa;

    public Musica(String nome, String artista, String caminhoArquivo, String caminhoCapa){
        this.nome = nome;
        this.artista = artista;
        this.caminhoArquivo = caminhoArquivo;
        this.caminhoCapa = caminhoCapa;
    }

    public String getNome(){return nome;}
    public String getArtista(){return artista;}
    public String getCaminhoArquivo(){return caminhoArquivo;}
    public String getCaminhoCapa(){return caminhoCapa;}

    @Override
    public String toString(){
        return nome + " - " + artista;
    }
}