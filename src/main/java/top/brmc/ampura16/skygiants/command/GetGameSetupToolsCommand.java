package top.brmc.ampura16.skygiants.command;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import top.brmc.ampura16.skygiants.utils.IColorizable;

import java.util.List;

public class GetGameSetupToolsCommand implements SubCommand, IColorizable {
    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(colorize("&c只有玩家可以使用此命令."));
            return true;
        }

        Player player = (Player) sender;

        // 创建道具
        ItemStack setupTool = new ItemStack(Material.STICK);
        ItemMeta meta = setupTool.getItemMeta();
        meta.setDisplayName(colorize("&6地图设置工具"));
        setupTool.setItemMeta(meta);

        // 将道具给予玩家
        player.getInventory().addItem(setupTool);
        player.sendMessage(colorize("&a你已获得地图设置工具."));

        return true;
    }

    @Override
    public String getPermission() {
        return "skygiants.admin";
    }
}
