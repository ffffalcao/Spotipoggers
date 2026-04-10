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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.ImageIcon;

public class BuscadorCapas {
    // MusicBrainz API para buscar o MBID do release (limit=5 para mais opções)
    private static final String MUSICBRAINZ_API = "https://musicbrainz.org/ws/2/recording/?fmt=json&limit=5&query=";
    // Cover Art Archive endpoints
    private static final String COVERART_RELEASE = "https://coverartarchive.org/release/";
    private static final String COVERART_RELEASE_GROUP = "https://coverartarchive.org/release-group/";

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

                // 2. Extrair todos os IDs (release-groups e releases)
                List<String[]> ids = extrairTodosIds(mbJson);
                if (ids.isEmpty()) {
                    System.out.println("[BuscadorCapas] Nenhum release encontrado no MusicBrainz");
                    return;
                }
                System.out.println("[BuscadorCapas] Encontrados " + ids.size() + " IDs para tentar");

                // 3. Tentar release-groups primeiro (maior chance de ter capa)
                byte[] imgData = null;
                for (String[] par : ids) {
                    String tipo = par[0]; // "release-group" ou "release"
                    String id = par[1];
                    String baseUrl = tipo.equals("release-group") ? COVERART_RELEASE_GROUP : COVERART_RELEASE;
                    String coverUrl = baseUrl + id + "/front-500";
                    System.out.println("[BuscadorCapas] Tentando " + tipo + ": " + coverUrl);

                    imgData = baixarImagem(coverUrl);
                    if (imgData != null) {
                        System.out.println("[BuscadorCapas] Capa encontrada via " + tipo + " " + id);
                        break;
                    }
                }

                if (imgData == null) {
                    System.out.println("[BuscadorCapas] Nenhuma capa encontrada em nenhum ID");
                    return;
                }

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

    /**
     * Extrai todos os release-group IDs e release IDs do JSON do MusicBrainz.
     * Retorna pares [tipo, id] ordenados: release-groups primeiro (maior chance de capa).
     */
    private static List<String[]> extrairTodosIds(String json) {
        List<String[]> releaseGroups = new ArrayList<>();
        List<String[]> releases = new ArrayList<>();

        // Busca todas as ocorrências de release-group ids
        String rgMarcador = "\"release-group\"";
        int pos = 0;
        while (pos < json.length()) {
            int rgIdx = json.indexOf(rgMarcador, pos);
            if (rgIdx < 0) break;
            // Procura o "id" dentro deste release-group
            int idIdx = json.indexOf("\"id\"", rgIdx + rgMarcador.length());
            if (idIdx < 0) break;
            // Não avançar muito (pode ser outro objeto)
            if (idIdx - rgIdx > 50) { pos = rgIdx + rgMarcador.length(); continue; }
            String id = extrairValorAposIdx(json, idIdx);
            if (id != null && !jaExiste(releaseGroups, id)) {
                releaseGroups.add(new String[]{"release-group", id});
            }
            pos = idIdx + 4;
        }

        // Busca todas as ocorrências de releases
        String relMarcador = "\"releases\"";
        pos = 0;
        while (pos < json.length()) {
            int relIdx = json.indexOf(relMarcador, pos);
            if (relIdx < 0) break;
            // Dentro do array de releases, pode haver múltiplos objetos com "id"
            int arrayStart = json.indexOf("[", relIdx);
            if (arrayStart < 0) break;
            int arrayEnd = encontrarFimArray(json, arrayStart);
            if (arrayEnd < 0) arrayEnd = Math.min(json.length(), arrayStart + 2000);

            int searchPos = arrayStart;
            while (searchPos < arrayEnd) {
                int idIdx = json.indexOf("\"id\"", searchPos);
                if (idIdx < 0 || idIdx >= arrayEnd) break;
                String id = extrairValorAposIdx(json, idIdx);
                if (id != null && !jaExiste(releases, id)) {
                    releases.add(new String[]{"release", id});
                }
                searchPos = idIdx + 4;
            }
            pos = arrayEnd;
        }

        // Release-groups primeiro, depois releases
        List<String[]> todos = new ArrayList<>();
        todos.addAll(releaseGroups);
        todos.addAll(releases);
        return todos;
    }

    private static String extrairValorAposIdx(String json, int idIdx) {
        int doisPontos = json.indexOf(":", idIdx);
        if (doisPontos < 0) return null;
        int aspas1 = json.indexOf("\"", doisPontos);
        if (aspas1 < 0) return null;
        int aspas2 = json.indexOf("\"", aspas1 + 1);
        if (aspas2 <= aspas1) return null;
        String val = json.substring(aspas1 + 1, aspas2);
        // Validar formato UUID (36 chars com hífens)
        if (val.length() == 36 && val.chars().filter(c -> c == '-').count() == 4) return val;
        return null;
    }

    private static boolean jaExiste(List<String[]> lista, String id) {
        for (String[] par : lista) {
            if (par[1].equals(id)) return true;
        }
        return false;
    }

    private static int encontrarFimArray(String json, int start) {
        int depth = 0;
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[') depth++;
            else if (c == ']') { depth--; if (depth == 0) return i; }
        }
        return -1;
    }

    /**
     * Tenta baixar uma imagem de uma URL, seguindo redirecionamentos.
     * Retorna null se falhar (404, timeout, etc).
     */
    private static byte[] baixarImagem(String urlStr) {
        try {
            URL imgUrl = URI.create(urlStr).toURL();
            HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
            conn.setRequestProperty("User-Agent", "Spotipoggers/1.0 (https://github.com/ffffalcao/Spotipoggers)");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(15000);
            conn.setInstanceFollowRedirects(true);

            int status = conn.getResponseCode();
            // Cover Art Archive redireciona (3xx) para a imagem real
            if (status >= 300 && status < 400) {
                String location = conn.getHeaderField("Location");
                conn.disconnect();
                if (location != null) {
                    imgUrl = URI.create(location).toURL();
                    conn = (HttpURLConnection) imgUrl.openConnection();
                    conn.setRequestProperty("User-Agent", "Spotipoggers/1.0");
                    conn.setConnectTimeout(8000);
                    conn.setReadTimeout(15000);
                    status = conn.getResponseCode();
                }
            }

            if (status != 200) {
                conn.disconnect();
                return null;
            }

            InputStream is = conn.getInputStream();
            byte[] data = is.readAllBytes();
            is.close();
            conn.disconnect();
            return data;
        } catch (Exception e) {
            return null;
        }
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
