package passatempo;

//import java.io.File;

public class PlayerUniversal {
    private PlayerMp3 playerMp3 = new PlayerMp3();
    private PlayerWav playerWav = new PlayerWav();
    private PlayerFlac playerFlac = new PlayerFlac();
    
    public void tocar(String caminhoArquivo) {
        String extensao = getExtensao(caminhoArquivo).toLowerCase();
        
        try {
            switch (extensao) {
                case "mp3":
                    playerMp3.tocar(caminhoArquivo);
                    break;
                case "wav":
                    playerWav.tocar(caminhoArquivo);
                    break;
                case "flac":
                case "ogg":
                    playerFlac.tocar(caminhoArquivo);
                    break;
                default:
                    System.out.println("Formato não suportado: " + extensao);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void parar() {
        playerMp3.parar();
        playerWav.parar();
        playerFlac.parar();
    }
    
    public long getDuracao(String caminhoArquivo) {
        String extensao = getExtensao(caminhoArquivo).toLowerCase();
        
        switch (extensao) {
            case "wav":
                return playerWav.getDuracao();
            case "flac":
            case "ogg":
                return playerFlac.getDuracao();
            case "mp3":
            default:
                return 0; // MP3 não suporta duração precisa
        }
    }
    
    public long getPosicao(String caminhoArquivo) {
        String extensao = getExtensao(caminhoArquivo).toLowerCase();
        
        switch (extensao) {
            case "wav":
                return playerWav.getPosicao();
            case "flac":
            case "ogg":
                return playerFlac.getPosicao();
            case "mp3":
            default:
                return 0; // MP3 não suporta posição precisa
        }
    }
    
    private String getExtensao(String caminho) {
        int lastDot = caminho.lastIndexOf('.');
        return lastDot > 0 ? caminho.substring(lastDot + 1) : "";
    }
} 