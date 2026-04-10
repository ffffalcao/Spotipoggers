package passatempo;

public class PlayerUniversal {
    private PlayerMp3 playerMp3 = new PlayerMp3();
    private PlayerWav playerWav = new PlayerWav();
    private PlayerFlac playerFlac = new PlayerFlac();
    private PlayerInterface playerAtual = null;

    public void tocar(String caminhoArquivo) {
        parar();
        playerAtual = getPlayer(caminhoArquivo);
        if (playerAtual != null) {
            playerAtual.tocar(caminhoArquivo);
        }
    }

    public void pausar() {
        if (playerAtual != null) playerAtual.pausar();
    }

    public void retomar() {
        if (playerAtual != null) playerAtual.retomar();
    }

    public void parar() {
        if (playerAtual != null) {
            playerAtual.parar();
            playerAtual = null;
        }
    }

    public long getDuracao() {
        return playerAtual != null ? playerAtual.getDuracao() : 0;
    }

    public long getPosicao() {
        return playerAtual != null ? playerAtual.getPosicao() : 0;
    }

    public void setPosicao(long ms) {
        if (playerAtual != null) playerAtual.setPosicao(ms);
    }

    public boolean isTocando() {
        return playerAtual != null && playerAtual.isTocando();
    }

    public boolean isPausado() {
        return playerAtual != null && playerAtual.isPausado();
    }

    public void setVolume(float volume) {
        if (playerAtual != null) playerAtual.setVolume(volume);
    }

    private PlayerInterface getPlayer(String caminho) {
        String ext = getExtensao(caminho).toLowerCase();
        switch (ext) {
            case "mp3": return playerMp3;
            case "wav": return playerWav;
            case "flac":
            case "ogg": return playerFlac;
            default:
                System.out.println("Formato não suportado: " + ext);
                return null;
        }
    }

    private String getExtensao(String caminho) {
        int lastDot = caminho.lastIndexOf('.');
        return lastDot > 0 ? caminho.substring(lastDot + 1) : "";
    }
} 