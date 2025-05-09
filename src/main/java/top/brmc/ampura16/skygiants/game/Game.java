package top.brmc.ampura16.skygiants.game;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.plugin.java.JavaPlugin;
import top.brmc.ampura16.skygiants.config.GameConfigManager;
import top.brmc.ampura16.skygiants.events.SkyGiantsOpenTeamSelectionEvent;
import top.brmc.ampura16.skygiants.utils.IColorizable;
import top.brmc.ampura16.skygiants.scoreboard.ScoreboardManager;

import java.util.*;

public class Game implements IColorizable {
    private final JavaPlugin plugin;
    private final String teamName;
    private Location lobby;
    private GameState state;
    private int minPlayers;
    private int maxPlayers;
    private final Set<Player> players = new HashSet<>();
    private final Map<String, Team> teams = new HashMap<>(); // 队伍管理
    private Location gameSpawn;
    private final GameConfigManager configManager;
    private boolean autobalanceEnabled = false;
    private final Map<Player, PlayerStorage> playerStorages = new HashMap<>(); // 玩家存储映射
    private final Map<Player, Game> playerGames = new HashMap<>(); // 玩家与游戏的映射
    private final ScoreboardManager scoreboardManager; // 计分板管理器
    private StartCountdownTask startCountdownTask;

    // ========== 构造方法 ==========
    public Game(JavaPlugin plugin, String teamName, GameConfigManager configManager, ScoreboardManager scoreboardManager) {
        this.plugin = plugin; // 确保 plugin 字段正确初始化
        this.teamName = teamName;
        this.configManager = configManager;
        this.scoreboardManager = scoreboardManager; // 初始化计分板管理器
        this.state = GameState.WAITING;
        this.minPlayers = 2;
        this.maxPlayers = 8;
    }

    // ========== Getter 和 Setter 方法 ==========
    public String getTeamName() { return teamName; }

    public Location getLobby() { return lobby; }
    public void setLobby(Location lobby) { this.lobby = lobby; }

    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }

    public int getMinPlayers() { return minPlayers; }
    public void setMinPlayers(int minPlayers) {
        this.minPlayers = Math.max(1, minPlayers);
    }

    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = Math.max(minPlayers, maxPlayers);
    }

    public Location getGameSpawn() { return gameSpawn; }
    public void setGameSpawn(Location gameSpawn) { this.gameSpawn = gameSpawn; }

    public boolean isAutobalanceEnabled() { return autobalanceEnabled; }
    public void setAutobalanceEnabled(boolean autobalanceEnabled) {
        this.autobalanceEnabled = autobalanceEnabled;
    }

    public int getPlayerCount() { return players.size(); }
    public Set<Player> getPlayers() { return new HashSet<>(players); }

    // ========== 玩家管理方法 ==========
    public PlayerStorage addPlayerStorage(Player p) {
        PlayerStorage storage = new PlayerStorage(p);
        this.playerStorages.put(p, storage);
        return storage;
    }

    public boolean joinPlayer(Player player) {
        if (state != GameState.WAITING || isFull() || players.contains(player)) {
            return false;
        }

        players.add(player);
        PlayerStorage storage = new PlayerStorage(player); // 初始化玩家存储
        playerStorages.put(player, storage);
        storage.loadLobbyInventory(this); // 加载大厅物品栏

        // 检测人数是否达到最小值
        if (players.size() >= minPlayers && startCountdownTask == null) {
            startCountdown();
        }

        return true;
    }

    public boolean leavePlayer(Player player) {
        boolean removed = players.remove(player);
        if (removed && state == GameState.RUNNING && players.size() < minPlayers) {
            stop();
        }

        // 移除玩家存储
        // playerStorages.remove(player);

        // 移除玩家背包
        player.getInventory().clear();

        // 重置玩家的显示名称和玩家列表名称
        resetPlayerDisplayName(player);

        // 重置玩家的计分板
        scoreboardManager.resetScoreboard(player);

        // 如果玩家离开后人数不足，取消倒计时
        if (players.size() < minPlayers && startCountdownTask != null) {
            startCountdownTask.cancel();
            startCountdownTask = null;
            sendMessageToAllPlayers(colorize("&c房间人数不足,倒计时取消."));
        }

        // 更新所有剩余玩家的计分板
        for (Player remainingPlayer : players) {
            if (remainingPlayer.isOnline()) {
                scoreboardManager.updateWaitingScoreboard(this);
            }
        }

        return removed;
    }

    private void resetPlayerDisplayName(Player player) {
        // 恢复玩家的显示名称
        player.setDisplayName(player.getName());

        // 恢复玩家的玩家列表名称
        player.setPlayerListName(player.getName());
    }

    // ========== 队伍管理方法 ==========
    public boolean createTeam(String teamName, TeamColor color, int maxPlayers) {
        if (teams.containsKey(teamName)) {
            return false; // 队伍已存在
        }
        Team team = new Team(teamName, color, maxPlayers, configManager, this.teamName);
        teams.put(teamName, team);
        return true;
    }


    public boolean removeTeam(String teamName) {
        Team team = teams.get(teamName);
        if (team == null) {
            return false;
        }

        // 移除队伍中的所有玩家
        for (Player player : team.getPlayers()) {
            leaveTeam(player);
        }

        // 从队伍映射中移除
        teams.remove(teamName);
        return true;
    }

    public boolean joinTeam(Player player, String teamName) {
        Team team = teams.get(teamName);
        if (team == null || team.isFull()) {
            return false;
        }

        // 如果玩家已经在其他队伍中，先退出
        leaveTeam(player);

        // 将玩家加入队伍
        team.addPlayer(player);
        playerGames.put(player, this);

        // 更新玩家的显示名称和计分板
        updatePlayerDisplayName(player);
        scoreboardManager.updateTeamSelectedScoreboard(this, team); // 更新队伍选择后的计分板

        return true;
    }

    public boolean leaveTeam(Player player) {
        for (Team team : teams.values()) {
            if (team.containsPlayer(player)) {
                return team.removePlayer(player);
            }
        }
        return false;
    }

    public Team getPlayerTeam(Player player) {
        for (Team team : teams.values()) {
            if (team.containsPlayer(player)) {
                return team;
            }
        }
        return null; // 如果玩家不在任何队伍中，返回 null
    }

    public Team getTeam(String teamName) {
        return teams.get(teamName); // 如果队伍不存在，返回 null
    }

    public HashMap<String, Team> getTeams() {
        return new HashMap<>(teams); // 返回队伍的副本
    }

    public boolean isSpectator(Player player) {
        // 假设观察者不在任何队伍中
        return getPlayerTeam(player) == null;
    }

    public boolean isInGame(Player player) {
        return players.contains(player);
    }

    // ========== 游戏状态控制方法 ==========
    public boolean start() {
        if (canStart()) return false;
        state = GameState.RUNNING;

        // 更新所有玩家的计分板为 ingame 格式
        updateIngameScoreboard();

        return true;
    }

    public void stop() {
        if (state == GameState.STOPPED) return;
        state = GameState.STOPPED;
        players.clear();
    }

    public boolean reset() {
        if (state == GameState.RUNNING) return false;
        state = GameState.WAITING;
        players.clear();
        return true;
    }

    // ========== 游戏状态检查方法 ==========
    public boolean canStart() {
        return state != GameState.WAITING
                || players.size() < minPlayers
                || players.size() > maxPlayers;
    }

    public boolean isRunning() {
        return state == GameState.RUNNING;
    }

    public boolean isFull() { return players.size() >= maxPlayers; }
    public boolean isEmpty() { return players.isEmpty(); }
    public boolean containsPlayer(Player player) { return players.contains(player); }

    // ========== 计分板更新方法 ==========
    public void updateIngameScoreboard() {
        for (Player player : players) {
            if (player.isOnline()) {
                scoreboardManager.updateIngameScoreboard(this, player);
            }
        }
    }

    // ========== 其他辅助方法 ==========
    public void openTeamSelection(Player player) {
        if (plugin == null) {
            System.out.println("Plugin is null in openTeamSelection!");
            return;
        }

        PlayerStorage storage = playerStorages.get(player);
        if (storage != null) {
            SkyGiantsOpenTeamSelectionEvent openEvent = new SkyGiantsOpenTeamSelectionEvent(this, player);
            plugin.getServer().getPluginManager().callEvent(openEvent);

            if (openEvent.isCancelled()) {
                return;
            }

            HashMap<String, Team> teams = getTeams();

            int nom = (teams.size() % 9 == 0) ? 9 : (teams.size() % 9);
            Inventory inv = plugin.getServer().createInventory(player, teams.size() + (9 - nom), colorize("&a选择队伍"));

            for (Team team : teams.values()) {
                if (team.getPlayers().size() >= team.getMaxPlayers()) {
                    continue; // 队伍已满，跳过
                }

                Wool wool = new Wool(team.getTeamColor().getDyeColor());
                ItemStack is = wool.toItemStack(1);
                ItemMeta im = is.getItemMeta();

                im.setDisplayName(colorize(team.getChatColor() + team.getTeamName()));
                ArrayList<String> teamplayers = new ArrayList<>();

                int teamPlayerSize = team.getPlayers().size();
                int maxPlayers = team.getMaxPlayers();

                String current;
                if (teamPlayerSize >= maxPlayers) {
                    current = colorize("&c" + teamPlayerSize);
                } else {
                    current = colorize("&e" + teamPlayerSize);
                }

                teamplayers.add(colorize("&7(" + current + "&7/&e" + maxPlayers + "&7)"));
                teamplayers.add(colorize("&f---------"));

                for (Player teamPlayer : team.getPlayers()) {
                    teamplayers.add(colorize(team.getChatColor() + stripColor(teamPlayer.getDisplayName())));
                }

                im.setLore(teamplayers);
                is.setItemMeta(im);
                inv.addItem(is);
            }

            player.openInventory(inv);
        }
    }

    public void sendMessageToAllPlayers(String message) {
        for (Player player : players) {
            if (player.isOnline()) {
                player.sendMessage(message);
            }
        }
    }

    public void setCountdownTask(StartCountdownTask startCountdownTask) {
        this.startCountdownTask = startCountdownTask;
    }

    private void startCountdown() {
        if (startCountdownTask != null) {
            return; // 倒计时已启动
        }

        int countdownTime = 10; // 倒计时时间（秒）
        startCountdownTask = new StartCountdownTask(this, countdownTime);
        startCountdownTask.runTaskTimer(plugin, 0L, 20L); // 每秒执行一次
    }

    private void updatePlayerDisplayName(Player player) {
        Team team = getPlayerTeam(player);
        if (team != null) {
            String displayName = team.getChatColor() + stripColor(player.getName());
            String playerListName = team.getChatColor() + team.getTeamName() + " &f| "
                    + team.getChatColor() + stripColor(player.getDisplayName());

            player.setDisplayName(colorize(displayName));
            player.setPlayerListName(colorize(playerListName));
        }
    }

    @Override
    public String toString() {
        return "Game{" +
                "name='" + teamName + '\'' +
                ", state=" + state +
                ", players=" + players.size() + "/" + maxPlayers +
                '}';
    }

    // ========== 配置文件相关方法 ==========
    public boolean saveTeamToConfig(String teamName, TeamColor color, int maxPlayers) {
        YamlConfiguration config = configManager.getGameConfig(this.teamName);
        if (config == null) {
            return false;
        }

        // 保存队伍信息
        config.set("teams." + teamName + ".color", color.name());
        config.set("teams." + teamName + ".maxPlayers", maxPlayers);

        // 保存配置文件
        return configManager.saveGameConfig(this.teamName, config);
    }

    public boolean removeTeamFromConfig(String teamName) {
        YamlConfiguration config = configManager.getGameConfig(this.teamName);
        if (config == null) {
            return false;
        }

        // 删除队伍信息
        config.set("teams." + teamName, null);

        // 保存配置文件
        return configManager.saveGameConfig(this.teamName, config);
    }

    /**
     * 保存队伍出生点到配置文件
     */
    public boolean saveTeamSpawnToConfig(String teamName, Location location) {
        YamlConfiguration config = configManager.getGameConfig(this.teamName);
        if (config == null) {
            return false;
        }

        // 保存队伍出生点（以序列化格式）
        config.set("teams." + teamName + ".teamSpawnLocation", serializeLocation(location));

        // 保存配置文件
        return configManager.saveGameConfig(this.teamName, config);
    }

    /**
     * 保存队伍巨人生成位置到配置文件
     */
    public boolean saveTeamGiantSpawnToConfig(String teamName, Location location) {
        YamlConfiguration config = configManager.getGameConfig(this.teamName);
        if (config == null) {
            return false;
        }

        // 保存队伍出生点（以序列化格式）
        config.set("teams." + teamName + ".teamGiantSpawnLocation", serializeLocation(location));

        // 保存配置文件
        return configManager.saveGameConfig(this.teamName, config);
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

    // ========== 队伍玩家信息获取方法 ==========
    public Map<String, List<Player>> getTeamPlayersInfo() {
        Map<String, List<Player>> teamPlayersInfo = new HashMap<>();

        // 遍历所有队伍
        for (Map.Entry<String, Team> entry : teams.entrySet()) {
            String teamName = entry.getKey();
            Team team = entry.getValue();

            // 获取该队伍的玩家列表
            List<Player> players = new ArrayList<>(team.getPlayers());

            // 将队伍名称和玩家列表添加到结果中
            teamPlayersInfo.put(teamName, players);
        }

        return teamPlayersInfo;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        return false;
    }
}
