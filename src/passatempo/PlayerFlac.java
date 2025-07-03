package passatempo;

import uk.co.caprica.vlcj.player.component.AudioPlayerComponent;

public class PlayerFlac {
    //Componente do VLCJ para reproduzir o áudio.
    private final AudioPlayerComponent player;

    public PlayerFlac(){
    player = new AudioPlayerComponent();
    }

    public void tocar(String caminhoArquivo) {
        //Reproduz o arquivo de áudio. (Pode ser FLAC, OGG e etc)
        player.mediaPlayer().media().play(caminhoArquivo);
    }

    public void parar() {
        //Para a reprodução do áudio.
        player.mediaPlayer().controls().stop();
    }
}