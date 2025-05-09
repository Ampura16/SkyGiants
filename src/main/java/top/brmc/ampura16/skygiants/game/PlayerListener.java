package top.brmc.ampura16.skygiants.game;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import top.brmc.ampura16.skygiants.utils.IColorizable;
import top.brmc.ampura16.skygiants.utils.SetupGameTools;

import java.util.List;

import static top.brmc.ampura16.skygiants.utils.SetupGameTools.openSetupGUI;

public class PlayerListener implements Listener, IColorizable {
    private final JavaPlugin plugin;
    private final GameManager gameManager;

    public PlayerListener(JavaPlugin plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
    }

    /**
     * 获取聊天格式，支持 PAPI 占位符
     *
     * @param format       聊天格式模板
     * @param player       玩家
     * @param team         玩家队伍
     * @param isSpectator  是否为观察者
     * @param isAllChat    是否为全体聊天
     * @return 解析后的聊天格式
     */
    private String getChatFormat(String format, Player player, Team team, boolean isSpectator, boolean isAllChat) {
        String form = format;

        // 替换 PAPI 占位符
        form = PlaceholderAPI.setPlaceholders(player, form);

        // 替换自定义占位符
        form = form.replace("{player}", player.getName());
        form = form.replace("{msg}", "%2$s");

        if (isSpectator) {
            form = form.replace("{team}", "观察者");
        } else if (team != null) {
            form = form.replace("{team}", team.getDisplayName());
        }

        // 在冒号前重置颜色
        form = form.replace(":", ChatColor.RESET + ":");

        return ChatColor.translateAlternateColorCodes('&', form);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        Game game = gameManager.getPlayerGame(player);

        if (game == null) {
            // 如果玩家不在游戏中，直接返回
            return;
        }

        Team team = game.getPlayerTeam(player);
        boolean isSpectator = game.isSpectator(player);
        String message = event.getMessage();

        // 判断是否为全体聊天
        boolean isAllChat = message.startsWith("@");
        if (isAllChat) {
            message = message.substring(1).trim();
        }

        // 获取聊天格式
        String format;
        if (isAllChat) {
            format = plugin.getConfig().getString("chat-format.ingame-all", "[全体] {team} {player}: {msg}");
        } else if (game.getState() == GameState.WAITING) {
            format = plugin.getConfig().getString("chat-format.lobby", "{team} {player}: {msg}");
        } else {
            format = plugin.getConfig().getString("chat-format.ingame", "{team} {player}: {msg}");
        }

        // 解析聊天格式
        String parsedFormat = getChatFormat(format, player, team, isSpectator, isAllChat);

        // 设置聊天格式和消息
        event.setFormat(parsedFormat);
        event.setMessage(message);

        event.getRecipients().clear(); // 清空默认接收者

        // 过滤聊天接收者
        if (game.getState() == GameState.WAITING) {
            // 大厅模式下,所有玩家可见
            event.getRecipients().addAll(game.getPlayers());
        } else if (isAllChat) {
            // 全体聊天模式下,同一游戏房间内所有玩家可见
            event.getRecipients().addAll(game.getPlayers());
        } else {
            // 队伍聊天模式下,同一队伍的玩家可见
            if (team != null) {
                event.getRecipients().addAll(team.getPlayers());
            }
        }

    }

    public ItemStack createZombieHead() {
        // 创建一个 SKULL 物品
        ItemStack zombieHead = new ItemStack(Material.SKULL, 1, (short) SkullType.ZOMBIE.ordinal());

        // 获取 SkullMeta
        SkullMeta meta = (SkullMeta) zombieHead.getItemMeta();

        // 设置显示名称
        meta.setDisplayName("§6地图设置工具");

        // 将 meta 应用到物品
        zombieHead.setItemMeta(meta);

        return zombieHead;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        // 检查是否点击了僵尸头
        if (item.getType() == Material.STICK
                && item.hasItemMeta()
                && item.getItemMeta().getDisplayName().equals("§6地图设置工具")) {
            event.setCancelled(true);
            openSetupGUI(player); // 打开空的 3x9 GUI
            return;
        }

        Game game = gameManager.getPlayerGame(player);
        if (game == null) {
            return;
        }

        switch (item.getType()) {
            case BED: // 选择队伍
                System.out.println("Player clicked BED item to select team.");
                game.openTeamSelection(player);
                break;
            case DIAMOND: // 开始游戏
                if (player.hasPermission("skygiants.admin") || player.isOp()) {
                    System.out.println("Player clicked DIAMOND item to start game.");
                    game.start();
                }
                break;
            case SLIME_BALL: // 退出房间
                System.out.println("Player clicked SLIME_BALL item to leave game.");
                gameManager.leaveGame(player);
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        Inventory clickedInventory = event.getClickedInventory();

        // 检查是否点击了有效的物品
        if (clickedInventory == null || clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        // 检查是否是队伍选择 GUI
        if (event.getView().getTitle().equals(colorize("&a选择队伍"))) {
            event.setCancelled(true); // 取消事件，防止玩家移动物品

            // 获取点击的队伍名称
            String teamName = stripColor(clickedItem.getItemMeta().getDisplayName());

            // 获取游戏实例
            Game game = gameManager.getPlayerGame(player);
            if (game == null) {
                return;
            }

            // 加入队伍
            Team team = game.getTeam(teamName);
            if (team != null && !team.isFull()) {
                game.joinTeam(player, teamName);
                player.closeInventory(); // 关闭 GUI
                player.sendMessage(colorize("&a你已加入队伍: " + team.getChatColor() + teamName));
            } else {
                player.sendMessage(colorize("&c无法加入该队伍，队伍已满或不存在。"));
            }
        }
    }

    @EventHandler
    public void onSetupToolsInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        Inventory clickedInventory = event.getClickedInventory();

        // 检查是否点击了有效的物品
        if (clickedInventory == null || clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        // 检查是否是地图设置工具 GUI
        if (event.getView().getTitle().equals(colorize(SetupGameTools.SETUP_GAME_GUI_TITLE))) {
            event.setCancelled(true); // 取消事件，防止玩家移动物品

            // 检查玩家点击的按钮
            if (clickedItem.hasItemMeta()) {
                String displayName = clickedItem.getItemMeta().getDisplayName();

                // 测试按钮
                if (displayName.equals(colorize(SetupGameTools.BUTTON_DISPLAY_NAME))) {
                    player.sendMessage(colorize("&a你点击了测试icon按钮."));
                }

                // 校正玩家位置按钮
                if (displayName.equals(colorize(SetupGameTools.CORRECT_PLAYER_LOCATION_NAME))) {
                    centerPlayerPosition(player); // 校正玩家位置
                    player.sendMessage(colorize("&a坐标已校正."));
                }
            }
        }
    }

    /**
     * 将玩家的位置校正为当前方块的中心位置
     *
     * @param player 玩家对象
     */
    private void centerPlayerPosition(Player player) {
        Location location = player.getLocation();

        // 校正 X 和 Z 坐标为方块中心
        location.setX(Math.floor(location.getX()) + 0.5);
        location.setZ(Math.floor(location.getZ()) + 0.5);

        // 校正 Yaw 为 90 的倍数，目视正前方
        float yaw = location.getYaw();
        float correctedYaw = Math.round(yaw / 90) * 90; // 将 yaw 校正为最近的 90 的倍数
        location.setYaw(correctedYaw);

        // 校正 Pitch 为 0，目视正前方
        location.setPitch(0);

        // 传送玩家
        player.teleport(location);
    }


    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        return false;
    }
}
