package passatempo;

public interface PlayerInterface {
    void tocar(String caminhoArquivo);
    void pausar();
    void retomar();
    void parar();
    long getDuracao();
    long getPosicao();
    void setPosicao(long ms);
    boolean isTocando();
    boolean isPausado();
    void setVolume(float volume);
}
