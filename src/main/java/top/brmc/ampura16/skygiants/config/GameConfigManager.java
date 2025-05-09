package top.brmc.ampura16.skygiants.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameConfigManager {
    private final JavaPlugin plugin;
    private final File gamesFolder;
    private YamlConfiguration config;

    public GameConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.gamesFolder = new File(plugin.getDataFolder(), "games");

        if (!gamesFolder.exists()) {
            gamesFolder.mkdirs();
        }

        // 加载配置文件
        loadConfig();
    }

    /**
     * 加载 config.yml 文件
     */
    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false); // 如果配置文件不存在，从插件资源中复制
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    /**
     * 获取已加载的 config.yml 配置
     */
    public YamlConfiguration getConfig() {
        return config;
    }

    public YamlConfiguration getGameConfig(String gameName) {
        File configFile = new File(gamesFolder, gameName + ".yml");

        if (!configFile.exists()) {
            return null;
        }

        return YamlConfiguration.loadConfiguration(configFile);
    }

    public boolean saveGameConfig(String gameName, YamlConfiguration config) {
        try {
            File gameFile = new File(plugin.getDataFolder(), "games/" + gameName + ".yml");
            if (!gameFile.exists()) {
                gameFile.getParentFile().mkdirs();
                gameFile.createNewFile();
            }
            config.save(gameFile); // 确保文件正确保存
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe("无法保存游戏配置文件: " + gameName);
            e.printStackTrace();
            return false;
        }
    }


    public boolean createGameConfig(String gameName) {
        File configFile = new File(gamesFolder, gameName + ".yml");

        if (configFile.exists()) {
            return false;
        }

        try {
            YamlConfiguration config = new YamlConfiguration();
            // 设置默认值
            config.set("name", gameName);
            config.set("state", "STOPPED");
            config.set("lobby", null);
            config.set("minPlayers", 2);
            config.set("maxPlayers", 8);

            config.save(configFile);
            return true;
        } catch (IOException e) {
            plugin.getLogger().severe("创建游戏配置失败: " + gameName);
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteGameConfig(String gameName) {
        File configFile = new File(gamesFolder, gameName + ".yml");

        if (!configFile.exists()) {
            return false;
        }

        return configFile.delete();
    }

    public List<String> getAllGameNames() {
        File[] files = gamesFolder.listFiles((dir, name) -> name.endsWith(".yml"));

        if (files == null) {
            return new ArrayList<>();
        }

        return Arrays.stream(files)
                .map(f -> f.getName().replace(".yml", ""))
                .collect(Collectors.toList());
    }
}
