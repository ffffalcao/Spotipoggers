package passatempo;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

public class PlayerMp3 implements PlayerInterface {
    private AdvancedPlayer player;
    private Thread thread;
    private String caminhoAtual;
    private volatile boolean tocando = false;
    private volatile boolean pausado = false;
    private volatile long posicaoInicioMs = 0;
    private volatile long tempoInicioPlayback = 0;
    private long duracaoMs = 0;
    private float msPerFrame = 0;

    @Override
    public void tocar(String caminhoArquivo) {
        parar();
        caminhoAtual = caminhoArquivo;
        posicaoInicioMs = 0;
        calcularDuracao(caminhoArquivo);
        iniciarReproducao(0);
    }

    @Override
    public void pausar() {
        if (tocando) {
            posicaoInicioMs = getPosicao();
            tocando = false;
            pausado = true;
            if (player != null) player.close();
        }
    }

    @Override
    public void retomar() {
        if (pausado && caminhoAtual != null) {
            int frame = msPerFrame > 0 ? (int) (posicaoInicioMs / msPerFrame) : 0;
            iniciarReproducao(frame);
        }
    }

    @Override
    public void parar() {
        tocando = false;
        pausado = false;
        posicaoInicioMs = 0;
        if (player != null) {
            player.close();
            player = null;
        }
    }

    @Override
    public long getDuracao() {
        return duracaoMs;
    }

    @Override
    public long getPosicao() {
        if (tocando) {
            return posicaoInicioMs + (System.currentTimeMillis() - tempoInicioPlayback);
        }
        return posicaoInicioMs;
    }

    @Override
    public void setPosicao(long ms) {
        if (caminhoAtual != null && msPerFrame > 0) {
            boolean estavaTocando = tocando;
            if (tocando) {
                tocando = false;
                if (player != null) player.close();
            }
            posicaoInicioMs = ms;
            if (estavaTocando) {
                int frame = (int) (ms / msPerFrame);
                iniciarReproducao(frame);
            }
        }
    }

    @Override
    public boolean isTocando() { return tocando; }

    @Override
    public boolean isPausado() { return pausado; }

    @Override
    public void setVolume(float volume) {
        // Controle de volume não suportado pelo JLayer
    }

    private void calcularDuracao(String caminho) {
        try (FileInputStream fis = new FileInputStream(caminho)) {
            Bitstream bitstream = new Bitstream(fis);
            Header header = bitstream.readFrame();
            if (header != null) {
                long fileSize = new File(caminho).length();
                msPerFrame = header.ms_per_frame();
                duracaoMs = (long) header.total_ms((int) fileSize);
            }
            bitstream.close();
        } catch (Exception e) {
            System.out.println("Erro ao calcular duração MP3: " + e.getMessage());
        }
    }

    private void iniciarReproducao(int frameInicial) {
        thread = new Thread(() -> {
            try {
                FileInputStream fis = new FileInputStream(caminhoAtual);
                BufferedInputStream bis = new BufferedInputStream(fis);
                player = new AdvancedPlayer(bis);
                tocando = true;
                pausado = false;
                tempoInicioPlayback = System.currentTimeMillis();
                player.play(frameInicial, Integer.MAX_VALUE);
            } catch (Exception e) {
                if (tocando) {
                    System.out.println("Erro ao reproduzir MP3: " + e.getMessage());
                }
            } finally {
                if (tocando) {
                    tocando = false;
                    posicaoInicioMs = duracaoMs;
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}