package top.brmc.ampura16.skygiants.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import top.brmc.ampura16.skygiants.Main;
import top.brmc.ampura16.skygiants.events.SkyGiantsPlayerSetNameEvent;
import top.brmc.ampura16.skygiants.utils.IColorizable;

import java.util.Collection;
import java.util.List;

public class PlayerStorage implements IColorizable {
    private ItemStack[] armor = null; // 保存玩家的装备
    private String displayName = null; // 保存玩家的显示名称
    private Collection<PotionEffect> effects = null; // 保存玩家的药水效果
    private int foodLevel = 0; // 保存玩家的饱食度
    private ItemStack[] inventory = null; // 保存玩家的物品栏
    private Location left = null; // 保存玩家离开时的位置
    private int level = 0; // 保存玩家的经验等级
    private String listName = null; // 保存玩家的列表名称
    private GameMode mode = null; // 保存玩家的游戏模式
    private final Player player; // 玩家对象
    private float xp = 0.0F; // 保存玩家的经验值

    public PlayerStorage(Player player) {
        this.player = player;
    }

    /**
     * 为玩家添加选择队伍的物品
     */
    public void addTeamSelectionItem() {
        ItemStack teamSelection = new ItemStack(Material.BED, 1);
        ItemMeta meta = teamSelection.getItemMeta();
        meta.setDisplayName(colorize("&a选择队伍"));
        teamSelection.setItemMeta(meta);
        player.getInventory().setItem(0, teamSelection); // 将物品放在第一个槽位
    }

    /**
     * 为玩家添加开始游戏的物品
     */
    public void addStartGameItem() {
        ItemStack startGame = new ItemStack(Material.DIAMOND, 1);
        ItemMeta meta = startGame.getItemMeta();
        meta.setDisplayName(colorize("&a开始游戏"));
        startGame.setItemMeta(meta);
        player.getInventory().setItem(1, startGame); // 将物品放在第二个槽位
    }

    /**
     * 为玩家添加退出房间的物品
     */
    public void addLeaveGameItem() {
        ItemStack leaveGame = new ItemStack(Material.SLIME_BALL, 1);
        ItemMeta meta = leaveGame.getItemMeta();
        meta.setDisplayName(colorize("&c退出房间"));
        leaveGame.setItemMeta(meta);
        player.getInventory().setItem(8, leaveGame); // 将物品放在最后一个槽位
    }

    /**
     * 清理玩家的状态
     */
    public void clean() {
        PlayerInventory inv = player.getInventory();
        inv.setArmorContents(new ItemStack[4]); // 清空装备
        inv.setContents(new ItemStack[]{}); // 清空物品栏

        // 重置玩家状态
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setExp(0.0F);
        player.setLevel(0);
        player.setSneaking(false);
        player.setSprinting(false);
        player.setFoodLevel(20);
        player.setSaturation(10);
        player.setExhaustion(0);
        player.setMaxHealth(20.0D);
        player.setHealth(20.0D);
        player.setFireTicks(0);

        // 重置玩家名称
        boolean teamnameOnTab = Main.getInstance().getConfig().getBoolean("teamname-on-tab", true);
        boolean overwriteNames = Main.getInstance().getConfig().getBoolean("overwrite-names", false);

        String displayName = player.getDisplayName();
        String playerListName = player.getPlayerListName();

        if (overwriteNames || teamnameOnTab) {
            Game game = Main.getInstance().getGameManager().getPlayerGame(player);
            if (game != null) {
                Team team = game.getPlayerTeam(player);

                if (overwriteNames) {
                    if (team != null) {
                        displayName = team.getChatColor() + ChatColor.stripColor(player.getName());
                    } else {
                        displayName = ChatColor.stripColor(player.getName());
                    }
                }

                if (teamnameOnTab) {
                    if (team != null) {
                        playerListName = team.getChatColor() + team.getTeamName() + ChatColor.WHITE + " | "
                                + team.getChatColor() + ChatColor.stripColor(player.getDisplayName());
                    } else {
                        playerListName = ChatColor.stripColor(player.getDisplayName());
                    }
                }

                // 当玩家选择队伍时更新TabList,会调用设置名称事件
                SkyGiantsPlayerSetNameEvent playerSetNameEvent =
                        new SkyGiantsPlayerSetNameEvent(team, displayName, playerListName, player);
                Bukkit.getPluginManager().callEvent(playerSetNameEvent);

                if (!playerSetNameEvent.isCancelled()) {
                    player.setDisplayName(playerSetNameEvent.getDisplayName());
                    player.setPlayerListName(playerSetNameEvent.getPlayerListName());
                }
            }
        }

        // 移除玩家的药水效果
        for (PotionEffect e : player.getActivePotionEffects()) {
            player.removePotionEffect(e.getType());
        }

        player.updateInventory();
    }

    /**
     * 加载大厅物品栏
     */
    public void loadLobbyInventory(Game game) {
        clean(); // 清理玩家物品栏

        // 添加选择队伍的物品（仅当自动平衡未启用时）
        if (!game.isAutobalanceEnabled()) {
            addTeamSelectionItem();
        }

        // 添加退出房间的物品
        addLeaveGameItem();

        // 添加开始游戏的物品（仅限有权限的玩家）
        if (player.hasPermission("skygiants.admin") || player.isOp()) {
            addStartGameItem();
        }

        player.updateInventory();
    }

    /**
     * 恢复玩家的状态
     */
    public void restore() {
        if (Main.getInstance().getConfig().getBoolean("save-inventory", true)) {
            player.getInventory().setContents(inventory); // 恢复物品栏
            player.getInventory().setArmorContents(armor); // 恢复装备

            player.addPotionEffects(effects); // 恢复药水效果
            player.setLevel(level); // 恢复经验等级
            player.setExp(xp); // 恢复经验值
            player.setFoodLevel(foodLevel); // 恢复饱食度

            for (PotionEffect e : player.getActivePotionEffects()) {
                player.removePotionEffect(e.getType()); // 移除当前药水效果
            }

            player.addPotionEffects(effects); // 重新添加药水效果
        }

        player.setPlayerListName(listName); // 恢复列表名称
        player.setDisplayName(displayName); // 恢复显示名称

        player.setGameMode(mode); // 恢复游戏模式

        if (mode == GameMode.CREATIVE) {
            player.setAllowFlight(true); // 允许飞行
        }
        player.updateInventory();
    }

    /**
     * 保存玩家的状态
     */
    public void store() {
        inventory = player.getInventory().getContents(); // 保存物品栏
        armor = player.getInventory().getArmorContents(); // 保存装备
        xp = player.getExp(); // 保存经验值
        effects = player.getActivePotionEffects(); // 保存药水效果
        mode = player.getGameMode(); // 保存游戏模式
        left = player.getLocation(); // 保存位置
        level = player.getLevel(); // 保存经验等级
        listName = player.getPlayerListName(); // 保存列表名称
        displayName = player.getDisplayName(); // 保存显示名称
        foodLevel = player.getFoodLevel(); // 保存饱食度
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        return false;
    }
}
