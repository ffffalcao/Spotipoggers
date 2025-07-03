package passatempo;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class PlayerWav {
    private Clip clip;

    public void tocar(String caminhoArquivo) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
    //Abre o arquivo de áudio
    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(caminhoArquivo));
    //Obtém um clip para tocar o áudio
    clip = AudioSystem.getClip();
    //Carrega o áudio no Clip
    clip.open(audioInputStream);
    //Inicia a reprodução
    clip.start();
    }

    public void parar() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}