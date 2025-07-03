package passatempo;
import javazoom.jl.player.Player;

import java.io.*;
public class PlayerMp3 {
    //Classe que vai reproduzir o arquivo mp3
    private Player player;

    public void tocar(String caminhoArquivo) throws Exception {
        FileInputStream fis = new FileInputStream(caminhoArquivo);
        player = new Player(fis);
        player.play(); //Inicia a reprodução da música.
    }

    public void parar(){
        if (player != null){
            player.close(); //Para a reprodução da música.
        }
    }
    
}