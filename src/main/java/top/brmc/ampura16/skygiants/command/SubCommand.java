package top.brmc.ampura16.skygiants.command;

import org.bukkit.command.CommandSender;
import top.brmc.ampura16.skygiants.utils.IColorizable;
import java.util.List;

public interface SubCommand extends IColorizable {
    boolean execute(CommandSender sender, List<String> args);

    default String getPermission() {
        return null; // 默认无权限要求
    }
}
