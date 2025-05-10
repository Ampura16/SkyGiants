package top.brmc.ampura16.skygiants.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import top.brmc.ampura16.skygiants.Main;
import top.brmc.ampura16.skygiants.config.GameConfigManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Team {
    private final String teamName;
    private final TeamColor teamColor;
    private final int maxPlayers;
    private final Set<Player> players = new HashSet<>();
    private Location teamSpawnLocation;
    private Location teamGiantSpawnLocation;
    private List<Block> chests = new ArrayList<>(); // 队伍箱子
    private Inventory inventory; // 队伍共享库存
    private org.bukkit.scoreboard.Team scoreboardTeam; // 使用 Bukkit 的 Team 类型

    private final GameConfigManager configManager; // 配置文件管理器
    private final String gameName; // 所属游戏名称

    public Team(String teamName,
                TeamColor teamColor,
                int maxPlayers,
                GameConfigManager configManager,
                String gameName
    ) {
        if (teamName == null || teamName.trim().isEmpty()) {
            throw new IllegalArgumentException("队伍名称不能为空");
        }
        if (gameName == null || gameName.trim().isEmpty()) {
            throw new IllegalArgumentException("游戏名称不能为空");
        }
        this.teamName = teamName;
        this.teamColor = teamColor;
        this.maxPlayers = maxPlayers;
        this.configManager = configManager;
        this.gameName = gameName;
    }

    /**
     * 添加玩家到队伍
     */
    public void addPlayer(Player player) {
        if (players.size() >= maxPlayers) {
            return; // 队伍已满
        }

        // 更新玩家的显示名称
        String displayName = getChatColor() + ChatColor.stripColor(player.getName());
        String playerTabListName = getChatColor() + teamName + ChatColor.WHITE + " | "
                + getChatColor() + ChatColor.stripColor(player.getDisplayName());

        player.setDisplayName(displayName);
        player.setPlayerListName(playerTabListName);

        players.add(player);
    }

    /**
     * 为玩家装备队伍颜色的皮革装备
     */
    private void equipPlayerWithLeather(Player player) {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET, 1);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.setColor(teamColor.getColor());
        helmet.setItemMeta(meta);

        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
        meta = (LeatherArmorMeta) chestplate.getItemMeta();
        meta.setColor(teamColor.getColor());
        chestplate.setItemMeta(meta);

        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
        meta = (LeatherArmorMeta) leggings.getItemMeta();
        meta.setColor(teamColor.getColor());
        leggings.setItemMeta(meta);

        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
        meta = (LeatherArmorMeta) boots.getItemMeta();
        meta.setColor(teamColor.getColor());
        boots.setItemMeta(meta);

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
        player.updateInventory();
    }

    /**
     * 移除玩家
     */
    public boolean removePlayer(Player player) {
        if (players.remove(player)) {
            if (Main.getInstance().getConfig().getBoolean("overwrite-names", false) && player.isOnline()) {
                player.setDisplayName(ChatColor.RESET + ChatColor.stripColor(player.getName()));
                player.setPlayerListName(ChatColor.RESET + player.getName());
            }

            // 从记分板队伍中移除玩家
            if (scoreboardTeam != null) {
                scoreboardTeam.removeEntry(player.getName());
            }

            return true;
        }
        return false;
    }

    /**
     * 检查玩家是否在队伍中
     */
    public boolean containsPlayer(Player player) {
        return players.contains(player);
    }

    /**
     * 检查队伍是否已满
     */
    public boolean isFull() {
        return players.size() >= maxPlayers;
    }

    /**
     * 获取队伍颜色对应的聊天颜色
     */
    public ChatColor getChatColor() {
        return teamColor.getChatColor();
    }

    /**
     * 获取队伍的显示名称
     */
    public String getDisplayName() {
        return teamColor.getChatColor() + teamName;
    }

    /**
     * 获取队伍的记分板队伍
     */
    public org.bukkit.scoreboard.Team getScoreboardTeam() {
        return scoreboardTeam;
    }

    /**
     * 设置队伍的记分板队伍
     */
    public void setScoreboardTeam(org.bukkit.scoreboard.Team scoreboardTeam) {
        if (scoreboardTeam != null) {
            scoreboardTeam.setDisplayName(getChatColor() + teamName); // 设置记分板队伍的显示名称
        }
        this.scoreboardTeam = scoreboardTeam;
    }

    /**
     * 创建队伍的共享库存
     */
    public void createTeamInventory() {
        inventory = Bukkit.createInventory(null, 27, "队伍共享库存");
    }

    /**
     * 获取队伍的共享库存
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * 添加箱子到队伍
     */
    public void addChest(Block chestBlock) {
        chests.add(chestBlock);
    }

    /**
     * 移除箱子
     */
    public void removeChest(Block chestBlock) {
        chests.remove(chestBlock);
        if (chests.isEmpty()) {
            inventory = null;
        }
    }

    /**
     * 获取队伍的箱子列表
     */
    public List<Block> getChests() {
        return chests;
    }

    // ========== Getter 和 Setter 方法 ==========
    public String getTeamName() { return teamName; }
    public TeamColor getTeamColor() { return teamColor; }

    /**
     * 获取队伍出生点
     */
    public Location getTeamSpawnLocation() {
        return teamSpawnLocation;
    }

    /**
     * 设置队伍出生点
     */
    public void setTeamSpawnLocation(Location teamSpawnLocation) {
        this.teamSpawnLocation = teamSpawnLocation;
    }

    public Set<Player> getPlayers() { return new HashSet<>(players); }
    public int getMaxPlayers() { return maxPlayers; }

    /**
     * 获取队伍巨人生成位置
     */
    public Location getTeamGiantSpawnLocation() {
        return teamGiantSpawnLocation;
    }

    /**
     * 设置队伍巨人生成位置
     */
    public void setTeamGiantSpawnLocation(Location teamGiantSpawnLocation) {
        this.teamGiantSpawnLocation = teamGiantSpawnLocation;
    }

    /**
     * 序列化 Location 为字符串
     */
    private String serializeLocation(Location location) {
        if (location == null) {
            return null;
        }
        return location.getWorld().getName() + "," +
                location.getX() + "," +
                location.getY() + "," +
                location.getZ() + "," +
                location.getYaw() + "," +
                location.getPitch();
    }
}
