package passatempo;

//import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class CarregadorMusicas {
    
    public static List<Musica> carregarDaPasta(String caminhoPasta) {
        List<Musica> musicas = new ArrayList<>();
        
        try {
            Path pasta = Paths.get(caminhoPasta);
            if (!Files.exists(pasta)) {
                System.out.println("Pasta não encontrada: " + caminhoPasta);
                return musicas;
            }
            
            // Lista todos os arquivos de música
            List<Path> arquivos = Files.walk(pasta)
                .filter(Files::isRegularFile)
                .filter(p -> isArquivoMusica(p.toString()))
                .collect(Collectors.toList());
            
            for (Path arquivo : arquivos) {
                String nomeArquivo = arquivo.getFileName().toString();
                String nomeMusica = getNomeSemExtensao(nomeArquivo);
                
                // Por enquanto, usa o nome do arquivo como artista também
                // Você pode melhorar isso depois
                Musica musica = new Musica(nomeMusica, "Artista", 
                                         arquivo.toString(), 
                                         "caminho/para/capa-padrao.jpg");
                musicas.add(musica);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return musicas;
    }
    
    private static boolean isArquivoMusica(String caminho) {
        String extensao = getExtensao(caminho).toLowerCase();
        return extensao.equals("mp3") || extensao.equals("wav") || 
               extensao.equals("flac") || extensao.equals("ogg");
    }
    
    private static String getExtensao(String caminho) {
        int lastDot = caminho.lastIndexOf('.');
        return lastDot > 0 ? caminho.substring(lastDot + 1) : "";
    }
    
    private static String getNomeSemExtensao(String nomeArquivo) {
        int lastDot = nomeArquivo.lastIndexOf('.');
        return lastDot > 0 ? nomeArquivo.substring(0, lastDot) : nomeArquivo;
    }
} 