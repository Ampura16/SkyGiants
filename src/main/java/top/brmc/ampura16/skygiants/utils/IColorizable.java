package top.brmc.ampura16.skygiants.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface IColorizable {
    default String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    default String stripColor(String text) {
        return ChatColor.stripColor(text);
    }

    default String getColoredMessage(String key, Object... args) {
        // 这里可以扩展为从语言文件获取带颜色的消息
        return colorize(String.format(key, args));
    }

    boolean execute(CommandSender sender, List<String> args);
}
