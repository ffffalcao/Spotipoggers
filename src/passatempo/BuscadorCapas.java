package passatempo;

import java.awt.Image;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class BuscadorCapas {
    private static final String ITUNES_API = "https://itunes.apple.com/search?media=music&limit=1&term=";
    private static final File PASTA_CACHE = new File(
        System.getProperty("java.io.tmpdir"), "spotipoggers-capas");
    private static final ConcurrentHashMap<String, ImageIcon> cacheMemoria = new ConcurrentHashMap<>();

    static {
        if (!PASTA_CACHE.exists()) PASTA_CACHE.mkdirs();
    }

    public interface Callback {
        void onCapaEncontrada(ImageIcon capa);
    }

    public static void buscarCapa(String artista, String musica, int tamanho, Callback callback) {
        String chave = normalizar(artista) + "_" + normalizar(musica);

        // Cache em memória
        ImageIcon cached = cacheMemoria.get(chave);
        if (cached != null) {
            callback.onCapaEncontrada(cached);
            return;
        }

        // Cache em disco
        File arquivoCache = new File(PASTA_CACHE, chave + ".jpg");
        if (arquivoCache.exists() && arquivoCache.length() > 0) {
            ImageIcon icon = carregarDeArquivo(arquivoCache, tamanho);
            if (icon != null) {
                cacheMemoria.put(chave, icon);
                callback.onCapaEncontrada(icon);
                return;
            }
        }

        // Busca online em thread separada
        Thread t = new Thread(() -> {
            try {
                // Só busca pelo nome se o artista for desconhecido
                String termo;
                if (artista == null || artista.equalsIgnoreCase("Desconhecido") || artista.isEmpty()) {
                    termo = musica;
                } else {
                    termo = artista + " " + musica;
                }
                System.out.println("[BuscadorCapas] Buscando capa para: " + termo);
                String query = URLEncoder.encode(termo, StandardCharsets.UTF_8);
                URL url = URI.create(ITUNES_API + query).toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Spotipoggers/1.0");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                if (conn.getResponseCode() != 200) {
                    System.out.println("[BuscadorCapas] API retornou status: " + conn.getResponseCode());
                    conn.disconnect();
                    return;
                }

                InputStream is = conn.getInputStream();
                byte[] data = is.readAllBytes();
                is.close();
                conn.disconnect();

                String json = new String(data, StandardCharsets.UTF_8);
                String artworkUrl = extrairArtworkUrl(json);
                if (artworkUrl == null) {
                    System.out.println("[BuscadorCapas] Nenhuma capa encontrada para: " + termo);
                    return;
                }
                System.out.println("[BuscadorCapas] Capa encontrada: " + artworkUrl);

                // Baixa a imagem
                URL imgUrl = URI.create(artworkUrl).toURL();
                HttpURLConnection imgConn = (HttpURLConnection) imgUrl.openConnection();
                imgConn.setRequestProperty("User-Agent", "Spotipoggers/1.0");
                imgConn.setConnectTimeout(5000);
                imgConn.setReadTimeout(10000);

                if (imgConn.getResponseCode() != 200) {
                    System.out.println("[BuscadorCapas] Erro ao baixar imagem: " + imgConn.getResponseCode());
                    imgConn.disconnect();
                    return;
                }

                InputStream imgIs = imgConn.getInputStream();
                byte[] imgData = imgIs.readAllBytes();
                imgIs.close();
                imgConn.disconnect();

                // Salva no cache
                try (OutputStream os = new FileOutputStream(arquivoCache)) {
                    os.write(imgData);
                }

                ImageIcon icon = carregarDeArquivo(arquivoCache, tamanho);
                if (icon != null) {
                    cacheMemoria.put(chave, icon);
                    System.out.println("[BuscadorCapas] Capa carregada com sucesso!");
                    callback.onCapaEncontrada(icon);
                }

            } catch (Exception e) {
                System.out.println("[BuscadorCapas] Erro: " + e.getMessage());
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private static String extrairArtworkUrl(String json) {
        // Tenta diversos formatos do campo artworkUrl
        String[] chaves = {"\"artworkUrl100\":\"", "\"artworkUrl100\" : \"",
                           "\"artworkUrl60\":\"", "\"artworkUrl60\" : \""};
        for (String chave2 : chaves) {
            int idx = json.indexOf(chave2);
            if (idx >= 0) {
                int inicio = idx + chave2.length();
                int fim = json.indexOf("\"", inicio);
                if (fim > inicio) {
                    String url = json.substring(inicio, fim);
                    // Normaliza para 600x600
                    return url.replaceAll("\\d+x\\d+bb", "600x600bb");
                }
            }
        }
        return null;
    }

    private static ImageIcon carregarDeArquivo(File arquivo, int tamanho) {
        try {
            ImageIcon raw = new ImageIcon(arquivo.getAbsolutePath());
            if (raw.getIconWidth() <= 0) return null;
            Image scaled = raw.getImage().getScaledInstance(tamanho, tamanho, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            return null;
        }
    }

    private static String normalizar(String s) {
        if (s == null) return "unknown";
        return s.toLowerCase()
                .replaceAll("[^a-z0-9]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
    }
}
