package passatempo;

import javax.sound.sampled.*;
import java.io.File;

public class PlayerFlac implements PlayerInterface {
    private Clip clip;
    private boolean pausado = false;
    private float volumeAtual = 0.8f;

    @Override
    public void tocar(String caminhoArquivo) {
        parar();
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(new File(caminhoArquivo));
            clip = AudioSystem.getClip();
            clip.open(ais);
            ais.close();
            aplicarVolume();
            clip.start();
            pausado = false;
        } catch (Exception e) {
            System.out.println("Erro ao tocar FLAC/OGG: " + e.getMessage());
            System.out.println("Use arquivos MP3 ou WAV para melhor compatibilidade.");
        }
    }

    @Override
    public void pausar() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            pausado = true;
        }
    }

    @Override
    public void retomar() {
        if (clip != null && pausado) {
            clip.start();
            pausado = false;
        }
    }

    @Override
    public void parar() {
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }
        pausado = false;
    }

    @Override
    public long getDuracao() {
        return clip != null ? clip.getMicrosecondLength() / 1000 : 0;
    }

    @Override
    public long getPosicao() {
        return clip != null ? clip.getMicrosecondPosition() / 1000 : 0;
    }

    @Override
    public void setPosicao(long ms) {
        if (clip != null) {
            boolean estavaTocando = clip.isRunning();
            clip.setMicrosecondPosition(ms * 1000);
            if (estavaTocando) clip.start();
        }
    }

    @Override
    public boolean isTocando() {
        return clip != null && clip.isRunning();
    }

    @Override
    public boolean isPausado() {
        return pausado;
    }

    @Override
    public void setVolume(float volume) {
        volumeAtual = Math.max(0f, Math.min(1f, volume));
        aplicarVolume();
    }

    private void aplicarVolume() {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = volumeAtual > 0 ? (float) (Math.log10(volumeAtual) * 20.0) : gc.getMinimum();
            dB = Math.max(gc.getMinimum(), Math.min(gc.getMaximum(), dB));
            gc.setValue(dB);
        }
    }
}