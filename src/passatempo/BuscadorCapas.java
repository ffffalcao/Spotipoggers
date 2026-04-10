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
import javax.swing.ImageIcon;

public class BuscadorCapas {
    // MusicBrainz API para buscar o MBID do release
    private static final String MUSICBRAINZ_API = "https://musicbrainz.org/ws/2/recording/?fmt=json&limit=1&query=";
    // Cover Art Archive para buscar a capa pelo MBID do release
    private static final String COVERART_API = "https://coverartarchive.org/release/";

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
                // 1. Busca no MusicBrainz pelo release MBID
                String query;
                if (artista == null || artista.equalsIgnoreCase("Desconhecido") || artista.isEmpty()) {
                    query = "recording:" + URLEncoder.encode(musica, StandardCharsets.UTF_8);
                } else {
                    query = "artist:" + URLEncoder.encode(artista, StandardCharsets.UTF_8)
                          + "+recording:" + URLEncoder.encode(musica, StandardCharsets.UTF_8);
                }
                System.out.println("[BuscadorCapas] Buscando no MusicBrainz: " + query);

                URL mbUrl = URI.create(MUSICBRAINZ_API + query).toURL();
                HttpURLConnection mbConn = (HttpURLConnection) mbUrl.openConnection();
                // MusicBrainz exige User-Agent com contato
                mbConn.setRequestProperty("User-Agent", "Spotipoggers/1.0 (https://github.com/ffffalcao/Spotipoggers)");
                mbConn.setConnectTimeout(8000);
                mbConn.setReadTimeout(8000);

                if (mbConn.getResponseCode() != 200) {
                    System.out.println("[BuscadorCapas] MusicBrainz retornou status: " + mbConn.getResponseCode());
                    mbConn.disconnect();
                    return;
                }

                InputStream mbIs = mbConn.getInputStream();
                String mbJson = new String(mbIs.readAllBytes(), StandardCharsets.UTF_8);
                mbIs.close();
                mbConn.disconnect();

                String releaseId = extrairReleaseId(mbJson);
                if (releaseId == null) {
                    System.out.println("[BuscadorCapas] Nenhum release encontrado no MusicBrainz");
                    return;
                }
                System.out.println("[BuscadorCapas] Release MBID: " + releaseId);

                // 2. Busca a capa no Cover Art Archive
                String coverUrl = COVERART_API + releaseId + "/front-500";
                System.out.println("[BuscadorCapas] Buscando capa em: " + coverUrl);

                URL imgUrl = URI.create(coverUrl).toURL();
                HttpURLConnection imgConn = (HttpURLConnection) imgUrl.openConnection();
                imgConn.setRequestProperty("User-Agent", "Spotipoggers/1.0");
                imgConn.setConnectTimeout(8000);
                imgConn.setReadTimeout(15000);
                imgConn.setInstanceFollowRedirects(true);

                int status = imgConn.getResponseCode();
                // Cover Art Archive redireciona (3xx) para a imagem real
                if (status >= 300 && status < 400) {
                    String location = imgConn.getHeaderField("Location");
                    imgConn.disconnect();
                    if (location != null) {
                        imgUrl = URI.create(location).toURL();
                        imgConn = (HttpURLConnection) imgUrl.openConnection();
                        imgConn.setRequestProperty("User-Agent", "Spotipoggers/1.0");
                        imgConn.setConnectTimeout(8000);
                        imgConn.setReadTimeout(15000);
                        status = imgConn.getResponseCode();
                    }
                }

                if (status != 200) {
                    System.out.println("[BuscadorCapas] Cover Art Archive retornou status: " + status);
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
                    System.out.println("[BuscadorCapas] Capa carregada com sucesso via Cover Art Archive!");
                    callback.onCapaEncontrada(icon);
                }

            } catch (Exception e) {
                System.out.println("[BuscadorCapas] Erro: " + e.getMessage());
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private static String extrairReleaseId(String json) {
        // Busca o primeiro "releases":[{"id":"<MBID>"
        String marcador = "\"releases\":[{\"id\":\"";
        int idx = json.indexOf(marcador);
        if (idx < 0) {
            // Tenta formato com espaço
            marcador = "\"releases\" : [{\"id\" : \"";
            idx = json.indexOf(marcador);
        }
        if (idx < 0) {
            // Fallback: busca qualquer "releases" seguido de "id"
            int relIdx = json.indexOf("\"releases\"");
            if (relIdx >= 0) {
                int idIdx = json.indexOf("\"id\"", relIdx);
                if (idIdx >= 0) {
                    int aspas1 = json.indexOf("\"", idIdx + 4);
                    if (aspas1 >= 0) {
                        int aspas2 = json.indexOf("\"", aspas1 + 1);
                        if (aspas2 >= 0) {
                            // Separa por : e pega o valor
                            int doisPontos = json.indexOf(":", idIdx);
                            if (doisPontos >= 0) {
                                aspas1 = json.indexOf("\"", doisPontos);
                                aspas2 = json.indexOf("\"", aspas1 + 1);
                                if (aspas1 >= 0 && aspas2 > aspas1) {
                                    String id = json.substring(aspas1 + 1, aspas2);
                                    if (id.length() == 36 && id.contains("-")) return id;
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }
        int inicio = idx + marcador.length();
        int fim = json.indexOf("\"", inicio);
        if (fim <= inicio) return null;
        String id = json.substring(inicio, fim);
        // Valida formato UUID
        if (id.length() == 36 && id.contains("-")) return id;
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
