package top.brmc.ampura16.skygiants.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.brmc.ampura16.skygiants.game.Game;
import top.brmc.ampura16.skygiants.game.GameManager;
import top.brmc.ampura16.skygiants.utils.IColorizable;

import java.util.List;

public class CreateSubCommand implements SubCommand, IColorizable {
    private final GameManager gameManager;
    private final String commandPrefix;

    public CreateSubCommand(GameManager gameManager, String commandPrefix) {
        this.gameManager = gameManager;
        this.commandPrefix = commandPrefix;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(colorize("&c只有玩家可以创建游戏房间"));
            return true;
        }

        if (args.size() < 1) {
            sender.sendMessage(colorize("&c用法: " + getCommandPrefix() + " &6create <房间名称>"));
            return true;
        }

        String gameName = args.get(0);
        Player player = (Player) sender;

        if (gameManager.getGame(gameName) != null) {
            sender.sendMessage(getColoredMessage("&c游戏房间 &e%s &c已存在", gameName));
            return true;
        }

        Game game = gameManager.createGame(gameName, player.getLocation());
        if (game != null) {
            sender.sendMessage(getColoredMessage("&a成功创建游戏房间 &e%s", gameName));
            return true;
        }

        sender.sendMessage(colorize("&c创建游戏房间失败"));
        return false;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }
}
