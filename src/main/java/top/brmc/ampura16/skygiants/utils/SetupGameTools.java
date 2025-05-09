package top.brmc.ampura16.skygiants.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SetupGameTools implements IColorizable {

    public static final String SETUP_GAME_GUI_TITLE = ChatColor.GOLD + "地图设置工具";
    public static final String BUTTON_DISPLAY_NAME = ChatColor.GREEN + "测试icon";
    public static final String CORRECT_PLAYER_LOCATION_NAME = ChatColor.GREEN + "校正坐标";

    /**
     * 打开一个空的 3x9 GUI
     *
     * @param player 玩家对象
     */
    public static void openSetupGUI(Player player) {

        Inventory gui = Bukkit.createInventory(player, 27, SETUP_GAME_GUI_TITLE);

        // 在 GUI 中添加一个Test按钮
        ItemStack testButton = new ItemStack(Material.STONE_BUTTON);
        ItemMeta meta = testButton.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(BUTTON_DISPLAY_NAME);
            testButton.setItemMeta(meta);
        }
        gui.setItem(13, testButton); // 将按钮放置在 GUI 的中心位置

        // 在 GUI 中添加校正玩家位置按钮
        ItemStack correctLocation = new ItemStack(Material.EMERALD);
        ItemMeta centerMeta = correctLocation.getItemMeta();
        if (centerMeta != null) {
            centerMeta.setDisplayName(CORRECT_PLAYER_LOCATION_NAME);
            correctLocation.setItemMeta(centerMeta);
        }
        gui.setItem(0, correctLocation);

        player.openInventory(gui); // 打开 GUI
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        return false;
    }
}
