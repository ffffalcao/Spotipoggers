package passatempo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE = System.getProperty("user.home")
            + File.separator + ".spotipoggers.properties";
    private Properties props = new Properties();

    public ConfigManager() {
        carregar();
    }

    public String getPastaMusicas() {
        return props.getProperty("pasta.musicas",
                System.getProperty("user.home") + File.separator + "Music");
    }

    public void setPastaMusicas(String pasta) {
        props.setProperty("pasta.musicas", pasta);
        salvar();
    }

    public float getVolume() {
        try {
            return Float.parseFloat(props.getProperty("volume", "0.8"));
        } catch (NumberFormatException e) {
            return 0.8f;
        }
    }

    public void setVolume(float volume) {
        props.setProperty("volume", String.valueOf(volume));
        salvar();
    }

    private void carregar() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
            } catch (IOException e) {
                // Primeira execução, usa valores padrão
            }
        }
    }

    private void salvar() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "Spotipoggers Config");
        } catch (IOException e) {
            System.out.println("Erro ao salvar configurações: " + e.getMessage());
        }
    }
}
