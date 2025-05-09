package top.brmc.ampura16.skygiants.config;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import top.brmc.ampura16.skygiants.utils.IColorizable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;

public class DefaultConfigManager implements IColorizable {
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private final File configFile;

    public DefaultConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        setupDefaultConfig();
    }

    /**
     * 设置默认配置
     */
    private void setupDefaultConfig() {
        // 确保插件数据目录存在
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        // 如果配置文件不存在，创建默认配置
        if (!configFile.exists()) {
            createDefaultConfig();
        }

        // 加载配置
        reloadConfig();
    }

    /**
     * 创建默认配置文件
     */
    private void createDefaultConfig() {
        try (InputStream inputStream = plugin.getResource("config.yml")) {
            if (inputStream == null) {
                plugin.getLogger().warning(colorize("&e无法找到默认 config.yml 资源"));
                return;
            }

            Files.copy(inputStream, configFile.toPath());
            plugin.getLogger().info(colorize("&a已创建默认 config.yml 配置文件"));
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, colorize("&c创建默认 config.yml 文件时出错"), e);
        }
    }

    /**
     * 重新加载配置
     */
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);

        // 设置默认值（如果配置项缺失）
        setDefaultsIfMissing();

        plugin.getLogger().info(colorize("&a配置文件已重新加载"));
    }

    /**
     * 保存配置
     */
    public void saveConfig() {
        try {
            config.save(configFile);
            plugin.getLogger().info(colorize("&a配置文件已保存"));
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, colorize("&c保存 config.yml 文件时出错"), e);
        }
    }

    /**
     * 获取配置
     */
    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    /**
     * 设置缺失的默认值
     */
    private void setDefaultsIfMissing() {
        // 默认游戏设置
        config.addDefault("default-game.lobby-wait-time", 60);

        // 消息配置
        config.addDefault("messages.game-start", "&a游戏已经开始!");
        config.addDefault("messages.game-stop", "&c游戏已被管理员停止");
        config.addDefault("messages.waiting-for-players", "&e等待更多玩家加入... (当前: %current%/%min%)");

        // 如果配置中有新增的默认值但文件没有这些键，则保存
        config.options().copyDefaults(true);
        saveConfig();
    }

    /**
     * 获取带颜色的配置消息
     * @param path 配置路径
     * @param args 格式化参数
     * @return 带颜色的消息
     */
    public String getColoredMessage(String path, Object... args) {
        String message = config.getString(path);
        if (message == null) {
            return colorize("&c未找到配置项: " + path);
        }
        return colorize(String.format(message, args));
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        return false;
    }
}
