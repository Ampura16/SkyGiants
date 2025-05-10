package top.brmc.ampura16.skygiants.game;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import top.brmc.ampura16.skygiants.config.GameConfigManager;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import top.brmc.ampura16.skygiants.scoreboard.ScoreboardManager;
import top.brmc.ampura16.skygiants.utils.IColorizable;

import java.util.*;
import java.util.logging.Logger;

public class GameManager implements IColorizable {
    private final Map<String, Game> games = new HashMap<>();
    private final Map<Player, Game> playerGames = new HashMap<>();
    private final JavaPlugin plugin;
    private final GameConfigManager configManager;
    private final ScoreboardManager scoreboardManager;
    private final Logger logger;
    private int defaultMinPlayers;
    private int defaultMaxPlayers;

    public GameManager(
            JavaPlugin plugin,
            GameConfigManager configManager,
            int defaultMinPlayers,
            int defaultMaxPlayers,
            ScoreboardManager scoreboardManager,
            Logger logger
    )
    {
        this.plugin = plugin;
        this.configManager = configManager;
        this.defaultMinPlayers = defaultMinPlayers;
        this.defaultMaxPlayers = defaultMaxPlayers;
        this.scoreboardManager = scoreboardManager;
        this.logger = logger;

        // 确保配置文件已加载
        configManager.loadConfig();
    }

    /**
     * 获取大厅计分板的标题
     */
    public String getLobbyScoreboardTitle() {
        return configManager.getConfig().getString("lobby-scoreboard.title", "&bSkyGiants");
    }

    /**
     * 获取大厅计分板的内容
     */
    public List<String> getLobbyScoreboardContent() {
        return configManager.getConfig().getStringList("lobby-scoreboard.content");
    }

    /**
     * 加载所有游戏 reload子命令最终调用方法
     * 添加config条目时记得同时更新saveGame和saveAllGames方法
     */
    public void loadAllGames() {
        List<String> gameNames = configManager.getAllGameNames();
        games.clear(); // 清空现有游戏

        for (String gameName : gameNames) {
            YamlConfiguration gameConfig = configManager.getGameConfig(gameName);

            if (gameConfig != null) {
                Game game = new Game(plugin, gameName, configManager, scoreboardManager);
                // 从配置加载游戏数据
                game.setLobby(deserializeLocation(gameConfig.getString("lobby"))); // 从字符串格式加载
                game.setState(GameState.valueOf(gameConfig.getString("state", "STOPPED")));
                game.setMinPlayers(gameConfig.getInt("minPlayers", defaultMinPlayers));
                game.setMaxPlayers(gameConfig.getInt("maxPlayers", defaultMaxPlayers));

                // 加载队伍数据
                if (gameConfig.contains("teams")) {
                    for (String teamName : gameConfig.getConfigurationSection("teams").getKeys(false)) {
                        TeamColor color = TeamColor.valueOf(gameConfig.getString("teams." + teamName + ".color"));
                        int maxPlayers = gameConfig.getInt("teams." + teamName + ".maxPlayers");
                        game.createTeam(teamName, color, maxPlayers);

                        // 加载队伍出生点
                        if (gameConfig.contains("teams." + teamName + ".teamSpawnLocation")) {
                            Location teamSpawnLocation = deserializeLocation(gameConfig.getString("teams." + teamName + ".teamSpawnLocation")); // 从字符串格式加载
                            if (teamSpawnLocation != null) {
                                game.getTeam(teamName).setTeamSpawnLocation(teamSpawnLocation);
                            }
                        }

                        // 加载队伍巨人生成位置
                        if (gameConfig.contains("teams." + teamName + ".teamGiantSpawnLocation")) {
                            Location teamGiantSpawnLocation = deserializeLocation(gameConfig.getString("teams." + teamName + ".teamGiantSpawnLocation")); // 从字符串格式加载
                            if (teamGiantSpawnLocation != null) {
                                game.getTeam(teamName).setTeamGiantSpawnLocation(teamGiantSpawnLocation);
                            }
                        }
                    }
                }
                games.put(gameName, game);
            }
        }
    }

    /**
     * 保存单个游戏
     */
    public void saveGame(Game game) {
        YamlConfiguration config = new YamlConfiguration();

        config.set("name", game.getTeamName());
        config.set("state", game.getState().name());
        config.set("lobby", serializeLocation(game.getLobby())); // 使用字符串格式保存
        config.set("minPlayers", game.getMinPlayers());
        config.set("maxPlayers", game.getMaxPlayers());

        // 保存队伍配置（如果存在）
        if (game.getTeams() != null && !game.getTeams().isEmpty()) {
            for (Map.Entry<String, Team> entry : game.getTeams().entrySet()) {
                String teamName = entry.getKey();
                Team team = entry.getValue();
                config.set("teams." + teamName + ".color", team.getTeamColor().name());
                config.set("teams." + teamName + ".maxPlayers", team.getMaxPlayers());
                config.set("teams." + teamName + ".teamSpawnLocation", serializeLocation(team.getTeamSpawnLocation())); // 保存队伍出生点
                config.set("teams." + teamName + ".teamGiantSpawnLocation", serializeLocation(team.getTeamGiantSpawnLocation())); // 保存队伍巨人生成位置
            }
        }

        configManager.saveGameConfig(game.getTeamName(), config);
    }

    /**
     * 重新加载所有游戏
     */
    public void reloadAllGames() {
        saveAllGames(); // 先保存当前状态
        loadAllGames(); // 重新加载
    }
    
    /**
     * 保存所有游戏配置
     */
    public void saveAllGames() {
        for (Game game : games.values()) {
            // 保存房间配置
            saveGame(game);

            // 保存队伍配置
            for (Map.Entry<String, Team> entry : game.getTeams().entrySet()) {
                String teamName = entry.getKey();
                Team team = entry.getValue();
                game.saveTeamToConfig(teamName, team.getTeamColor(), team.getMaxPlayers());
                game.saveTeamSpawnToConfig(teamName, team.getTeamSpawnLocation()); // 确保保存队伍出生点
                game.saveTeamGiantSpawnToConfig(teamName, team.getTeamGiantSpawnLocation()); // 确保保存队伍巨人生成位置
            }
        }
    }

    /**
     * 创建新游戏
     */
    public Game createGame(String gameName, Location lobby) {
        if (games.containsKey(gameName) || !configManager.createGameConfig(gameName)) {
            return null;
        }

        Game game = new Game(plugin, gameName, configManager, scoreboardManager);
        game.setLobby(lobby);
        game.setState(GameState.WAITING);
        game.setMinPlayers(defaultMinPlayers);
        game.setMaxPlayers(defaultMaxPlayers);

        games.put(gameName, game);
        saveGame(game);

        return game;
    }

    /**
     * 删除游戏
     */
    public boolean removeGame(String gameName) {
        Game game = games.get(gameName);
        if (game == null) {
            return false;
        }

        // 踢出所有玩家
        new ArrayList<>(game.getPlayers()).forEach(this::leaveGame);

        games.remove(gameName);
        return configManager.deleteGameConfig(gameName);
    }

    /**
     * 更新默认玩家数量限制
     */
    public void updateDefaultPlayerLimits(int minPlayers, int maxPlayers) {
        this.defaultMinPlayers = minPlayers;
        this.defaultMaxPlayers = maxPlayers;

        // 更新所有使用默认值的游戏
        games.values().stream()
                .filter(game -> game.getMinPlayers() == this.defaultMinPlayers)
                .forEach(game -> game.setMinPlayers(minPlayers));

        games.values().stream()
                .filter(game -> game.getMaxPlayers() == this.defaultMaxPlayers)
                .forEach(game -> game.setMaxPlayers(maxPlayers));
    }

    public List<String> getAllGameNames() {
        return new ArrayList<>(games.keySet());
    }

    public boolean joinGame(Player player, String gameName) {
        Game game = games.get(gameName);
        if (game == null || game.getState() != GameState.WAITING) {
            return false;
        }

        // 如果玩家已在其他游戏中，先退出
        if (playerGames.containsKey(player)) {
            leaveGame(player);
        }

        if (game.joinPlayer(player)) {
            playerGames.put(player, game);
            // 更新计分板
            scoreboardManager.updateWaitingScoreboard(game); // 传递 game 参数
            return true;
        }
        return false;
    }

    public boolean leaveGame(Player player) {
        Game game = playerGames.get(player);
        if (game == null) {
            return false;
        }

        if (game.leavePlayer(player)) {
            playerGames.remove(player);
            // 重置计分板
            scoreboardManager.resetScoreboard(player);
            return true;
        }
        return false;
    }

    /**
     * 获取玩家所在游戏实例
     * */
    public Game getPlayerGame(Player player) {
        return playerGames.get(player);
    }

    public Game getGame(String name) {
        return games.get(name);
    }

    /**
     * 序列化 Location 为字符串
     */
    private String serializeLocation(Location loc) {
        if (loc == null) {
            return null;
        }
        return loc.getWorld().getName() + "," +
                loc.getX() + "," +
                loc.getY() + "," +
                loc.getZ() + "," +
                loc.getYaw() + "," +
                loc.getPitch();
    }

    /**
     * 反序列化字符串为 Location
     */
    private Location deserializeLocation(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }

        String[] parts = str.split(",");
        if (parts.length != 6) {
            return null;
        }

        try {
            World world = Bukkit.getWorld(parts[0]);
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = Float.parseFloat(parts[4]);
            float pitch = Float.parseFloat(parts[5]);

            return new Location(world, x, y, z, yaw, pitch);
        } catch (Exception e) {
            return null;
        }
    }

    public Collection<Game> getAllGames() {
        return Collections.unmodifiableCollection(games.values());
    }

    /**
     * 获取默认最小玩家数
     */
    public int getDefaultMinPlayers() {
        return defaultMinPlayers;
    }

    /**
     * 获取默认最大玩家数
     */
    public int getDefaultMaxPlayers() {
        return defaultMaxPlayers;
    }

    public boolean createTeam(String gameName,String teamName, TeamColor color, int maxPlayers) {
        Game game = games.get(gameName);
        if (game == null) {
            return false;
        }
        return game.createTeam(teamName, color, maxPlayers);
    }

    public boolean joinTeam(Player player, String gameName, String teamName) {
        Game game = games.get(gameName);
        if (game == null) {
            return false;
        }
        return game.joinTeam(player, teamName);
    }

    public boolean leaveTeam(Player player) {
        Game game = playerGames.get(player);
        if (game == null) {
            return false;
        }
        return game.leaveTeam(player);
    }

    public Logger getLogger() {
        return logger;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        return false;
    }
}
