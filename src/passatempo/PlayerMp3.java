package passatempo;
import javazoom.jl.player.Player;
import java.io.FileInputStream;

public class PlayerMp3 {
    //Classe que vai reproduzir o arquivo mp3
    private Player player;
    private Thread thread;

    public void tocar(String caminhoArquivo){
        parar();
        thread = new Thread(() -> {
        try (FileInputStream fis = new FileInputStream(caminhoArquivo)){
            player = new Player(fis);
            player.play(); //Inicia a reprodução da música.
        } catch (Exception e){
            e.printStackTrace();
        }
    });
    thread.start();
    }

    public void parar(){
        if (player != null){
            player.close(); //Para a reprodução da música.
        }
    }
    
}