package passatempo;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class PlayerFlac {
    private Clip clip;
    private long duracaoTotal = 0;
    private long posicaoAtual = 0;
    private boolean suporteDisponivel = false;

    public PlayerFlac(){
        System.out.println("FLAC/OGG: Usando Java Sound (suporte limitado)");
        System.out.println("Para suporte completo a FLAC/OGG, atualize o VLC para uma versão compatível com VLCJ");
        suporteDisponivel = true;
    }

    public void tocar(String caminhoArquivo) {
        if (suporteDisponivel) {
            try {
                // Tenta usar Java Sound (funciona para alguns formatos)
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(caminhoArquivo));
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                duracaoTotal = clip.getMicrosecondLength() / 1000;
                clip.start();
            } catch (Exception e) {
                System.out.println("Erro ao tocar arquivo FLAC/OGG: " + e.getMessage());
                System.out.println("Use arquivos MP3 ou WAV para melhor compatibilidade.");
            }
        }
    }

    public void parar() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
    
    public long getDuracao() {
        return duracaoTotal;
    }
    
    public long getPosicao() {
        if (clip != null) {
            posicaoAtual = clip.getMicrosecondPosition() / 1000;
        }
        return posicaoAtual;
    }
}