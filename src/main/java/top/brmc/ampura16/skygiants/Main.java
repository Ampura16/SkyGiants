package top.brmc.ampura16.skygiants;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import top.brmc.ampura16.skygiants.command.SkyGiantsCommand;
import top.brmc.ampura16.skygiants.command.SkyGiantsTabCompleter;
import top.brmc.ampura16.skygiants.config.DefaultConfigManager;
import top.brmc.ampura16.skygiants.config.GameConfigManager;
import top.brmc.ampura16.skygiants.game.PlayerListener;
import top.brmc.ampura16.skygiants.game.GameManager;
import top.brmc.ampura16.skygiants.nms.v1_8_R3.GiantAIListener;
import top.brmc.ampura16.skygiants.nms.v1_8_R3.NMSEntityManager;
import top.brmc.ampura16.skygiants.placeholder.SkyGiantsPlaceholder;
import top.brmc.ampura16.skygiants.scoreboard.ScoreboardManager;

import java.io.File;

public class Main extends JavaPlugin {
    private static Main instance; // 单例实例
    private GameManager gameManager;
    private ScoreboardManager scoreboardManager;
    private NMSEntityManager nmsEntityManager;

    @Override
    public void onEnable() {
        instance = this; // 设置单例实例
        nmsEntityManager = NMSEntityManager.getInstance();

        try {
            initializePlugin();
            getLogger().info("SkyGiants 插件已启用");
        } catch (Exception e) {
            handlePluginError("插件启动失败", e);
        }
    }

    @Override
    public void onDisable() {
        try {
            if (gameManager != null) {
                gameManager.saveAllGames();
            }
            getLogger().info("SkyGiants 插件已禁用");
        } catch (Exception e) {
            handlePluginError("插件关闭时发生错误", e);
        }
    }

    /**
     * 插件启动逻辑
     * */
    private void initializePlugin() {
        // 打印logo
        printLogo();

        // 初始化配置管理器
        DefaultConfigManager defaultConfigManager = initializeDefaultConfigManager();
        GameConfigManager gameConfigManager = initializeGameConfigManager();

        // 初始化计分板管理器
        scoreboardManager = new ScoreboardManager(gameManager, getConfig());

        // 初始化游戏管理器
        gameManager = initializeGameManager(defaultConfigManager, gameConfigManager, scoreboardManager);

        // 注册玩家事件监听器
        getServer().getPluginManager().registerEvents(new PlayerListener(this, gameManager), this);
        getServer().getPluginManager().registerEvents(new GiantAIListener(), this);

        // 注册命令和补全器
        registerCommands();

        // 加载所有游戏配置
        gameManager.loadAllGames();

        // 注册 PlaceholderAPI 扩展
        registerPlaceholderAPI();
    }

    private DefaultConfigManager initializeDefaultConfigManager() {
        DefaultConfigManager defaultConfigManager = new DefaultConfigManager(this);
        getLogger().info("默认配置管理器已初始化");
        return defaultConfigManager;
    }

    private GameConfigManager initializeGameConfigManager() {
        GameConfigManager gameConfigManager = new GameConfigManager(this);
        getLogger().info("游戏配置管理器已初始化");
        return gameConfigManager;
    }

    private GameManager initializeGameManager(DefaultConfigManager defaultConfigManager,
                                              GameConfigManager gameConfigManager,
                                              ScoreboardManager scoreboardManager) {
        int minPlayers = defaultConfigManager.getConfig().getInt("default-game.min-players", 2);
        int maxPlayers = defaultConfigManager.getConfig().getInt("default-game.max-players", 8);

        GameManager gameManager = new GameManager(this, gameConfigManager, minPlayers, maxPlayers, scoreboardManager, getLogger());
        getLogger().info("游戏管理器已初始化");
        return gameManager;
    }

    private void registerCommands() {
        if (getCommand("SkyGiants") == null) {
            throw new IllegalStateException("无法注册命令 'SkyGiants', 请检查 plugin.yml 配置");
        }

        getCommand("SkyGiants").setExecutor(new SkyGiantsCommand(this, gameManager, initializeDefaultConfigManager()));
        getCommand("SkyGiants").setTabCompleter(new SkyGiantsTabCompleter(gameManager));
        getLogger().info("命令和补全器已注册");
    }

    private void registerPlaceholderAPI() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new SkyGiantsPlaceholder(gameManager).register();
            getLogger().info("已注册 PlaceholderAPI 扩展");
        } else {
            getLogger().warning("未找到 PlaceholderAPI，占位符功能将不可用");
        }
    }

    private void handlePluginError(String message, Exception e) {
        getLogger().severe(message + ": " + e.getMessage());
        e.printStackTrace();
        getServer().getPluginManager().disablePlugin(this);
    }

    public static Main getInstance() {
        return instance;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public NMSEntityManager getNMSEntityManager() {
        return nmsEntityManager;
    }

    public double getPluginFileSizeInKB() {
        File pluginFile = this.getFile();
        return pluginFile.length() / 1024.0;
    }

    /**
     * 打印 Logo
     */
    private void printLogo() {
        String[] logo = {
                "§b  ____  _           ____ _             _       ",
                "§b / ___|| | ___   _ / ___(_) __ _ _ __ | |_ ___ ",
                "§b \\___ \\| |/ / | | | |  _| |/ _` | '_ \\| __/ __|",
                "§b  ___) |   <| |_| | |_| | | (_| | | | | |_\\__ \\",
                "§b |____/|_|\\_\\\\__, |\\____|_|\\__,_|_| |_|\\__|___/",
                "§b             |___/"
        };

        String[] art = {
                "§d  __   ______  _   _   _                          _    _               _            _            ___ ",
                "§d  \\ \\ / / __ )| | | | (_)___  __      _____  _ __| | _(_)_ __   __ _  | |_ ___   __| | __ _ _   |__ \\",
                "§d   \\ V /|  _ \\| |_| | | / __| \\ \\ /\\ / / _ \\| '__| |/ / | '_ \\ / _` | | __/ _ \\ / _` |/ _` | | | |/ /",
                "§d    | | | |_) |  _  | | \\__ \\  \\ V  V / (_) | |  |   <| | | | | (_| | | || (_) | (_| | (_| | |_| |_| ",
                "§d    |_| |____/|_| |_| |_|___/   \\_/\\_/ \\___/|_|  |_|\\_\\_|_| |_|\\__, |  \\__\\___/ \\__,_|\\__,_|\\__, (_) ",
                "§d                                                              |___/                        |___/"
        };

        // 打印插件启用信息
        Bukkit.getLogger().info("[SkyGiants]");

        // 打印 Logo
        for (String line : logo) {
            getServer().getConsoleSender().sendMessage("§a[§bSkyGiants§a] " + line);
        }
        // 打印 ybh is working today?
        for (String line : art) {
            getServer().getConsoleSender().sendMessage("§a[§bSkyGiants§a] " + line);
        }

        Bukkit.getLogger().info("[SkyGiants]");

        // 打印作者信息
        Bukkit.getLogger().info("[SkyGiants] | SkyGiants (v" + getDescription().getVersion() + ") by " + getDescription().getAuthors().get(0));
    }
}
