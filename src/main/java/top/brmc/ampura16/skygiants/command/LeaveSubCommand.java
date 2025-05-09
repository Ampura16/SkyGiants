package top.brmc.ampura16.skygiants.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.brmc.ampura16.skygiants.game.GameManager;
import java.util.List;

public class LeaveSubCommand implements SubCommand {
    private final GameManager gameManager;

    public LeaveSubCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(colorize("&c只有玩家可以离开游戏房间"));
            return true;
        }

        Player player = (Player) sender;

        if (gameManager.getPlayerGame(player) == null) {
            sender.sendMessage(colorize("&c你当前不在任何游戏房间中"));
            return true;
        }

        if (gameManager.leaveGame(player)) {
            sender.sendMessage(colorize("&a已成功离开游戏房间"));
            return true;
        }

        sender.sendMessage(colorize("&c离开游戏房间失败"));
        return false;
    }
}
