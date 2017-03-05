package at.TimoCraft.TimoCloud.base.managers;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Timo on 31.01.17.
 */
public class FileManager {
    private String configsDirectory = "configs/";
    private String templatesDirectory = "templates/";
    private String temporaryDirectory = "temporary/";
    private String logsDirectory = "logs/";
    private File configFile;
    private Map<String, Object> config;

    public FileManager() {
        load();
    }

    public void load() {
        try {
            new File(getConfigsDirectory()).mkdirs();
            new File(getTemplatesDirectory()).mkdirs();
            new File(getTemporaryDirectory()).mkdirs();
            new File(getLogsDirectory()).mkdirs();
            this.configFile = new File(getConfigsDirectory(), "config.yml");
            if (! configFile.exists()) {
                Files.copy(this.getClass().getResourceAsStream("/base/config.yml"), configFile.toPath());
            }
            Yaml configYaml = new Yaml();
            this.config = (Map<String, Object>) configYaml.load(new FileReader(configFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getConfigsDirectory() {
        return configsDirectory;
    }

    public String getTemplatesDirectory() {
        return templatesDirectory;
    }

    public String getTemporaryDirectory() {
        return temporaryDirectory;
    }

    public String getLogsDirectory() {
        return logsDirectory;
    }

    public File getConfigFile() {
        return configFile;
    }

    public Map getConfig() {
        return config;
    }
}
