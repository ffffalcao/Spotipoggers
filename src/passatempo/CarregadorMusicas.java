package passatempo;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;

public class CarregadorMusicas {

    public static List<Musica> carregarDaPasta(String caminhoPasta) {
        List<Musica> musicas = new ArrayList<>();

        try {
            Path pasta = Paths.get(caminhoPasta);
            if (!Files.exists(pasta)) {
                System.out.println("Pasta não encontrada: " + caminhoPasta);
                return musicas;
            }

            List<Path> arquivos;
            try (Stream<Path> stream = Files.walk(pasta)) {
                arquivos = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> isArquivoMusica(p.toString()))
                    .collect(Collectors.toList());
            }

            for (Path arquivo : arquivos) {
                Musica musica = criarMusica(arquivo);
                musicas.add(musica);
            }

        } catch (Exception e) {
            System.out.println("Erro ao carregar músicas: " + e.getMessage());
        }

        return musicas;
    }

    private static Musica criarMusica(Path arquivo) {
        String nomeArquivo = arquivo.getFileName().toString();
        String nomeSemExt = getNomeSemExtensao(nomeArquivo);

        String nome;
        String artista;

        // Tenta parsear formato "Artista - Título"
        if (nomeSemExt.contains(" - ")) {
            String[] partes = nomeSemExt.split(" - ", 2);
            artista = partes[0].trim();
            nome = partes[1].trim();
        } else {
            nome = nomeSemExt;
            artista = "Desconhecido";
        }

        Musica musica = new Musica(nome, artista, arquivo.toString(), "");
        musica.setDuracaoMs(calcularDuracao(arquivo));
        return musica;
    }

    private static long calcularDuracao(Path arquivo) {
        String ext = getExtensao(arquivo.toString()).toLowerCase();
        try {
            if (ext.equals("mp3")) {
                return calcularDuracaoMp3(arquivo);
            } else {
                AudioInputStream ais = AudioSystem.getAudioInputStream(arquivo.toFile());
                AudioFormat format = ais.getFormat();
                long frames = ais.getFrameLength();
                ais.close();
                if (frames > 0 && format.getFrameRate() > 0) {
                    return (long) ((frames / format.getFrameRate()) * 1000);
                }
            }
        } catch (Exception e) {
            // Ignora, retorna 0
        }
        return 0;
    }

    private static long calcularDuracaoMp3(Path arquivo) {
        try (FileInputStream fis = new FileInputStream(arquivo.toFile())) {
            Bitstream bitstream = new Bitstream(fis);
            Header header = bitstream.readFrame();
            if (header != null) {
                long fileSize = arquivo.toFile().length();
                long duracao = (long) header.total_ms((int) fileSize);
                bitstream.close();
                return duracao;
            }
            bitstream.close();
        } catch (Exception e) {
            // Ignora
        }
        return 0;
    }

    private static boolean isArquivoMusica(String caminho) {
        String extensao = getExtensao(caminho).toLowerCase();
        return extensao.equals("mp3") || extensao.equals("wav")
            || extensao.equals("flac") || extensao.equals("ogg");
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