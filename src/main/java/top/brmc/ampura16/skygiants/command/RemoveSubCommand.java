package top.brmc.ampura16.skygiants.command;

import org.bukkit.command.CommandSender;
import top.brmc.ampura16.skygiants.game.Game;
import top.brmc.ampura16.skygiants.game.GameManager;
import top.brmc.ampura16.skygiants.game.GameState;
import java.util.List;

public class RemoveSubCommand implements SubCommand {
    private final GameManager gameManager;
    private final String commandPrefix;

    public RemoveSubCommand(GameManager gameManager, String commandPrefix) {
        this.gameManager = gameManager;
        this.commandPrefix = commandPrefix;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if (args.size() < 1) {
            sender.sendMessage(colorize("&c用法: " + getCommandPrefix() + " &6remove <房间名称>"));
            return true;
        }

        String gameName = args.get(0);
        Game game = gameManager.getGame(gameName);

        if (game == null) {
            sender.sendMessage(getColoredMessage("&c游戏房间 &e%s &c不存在", gameName));
            return true;
        }

        if (game.getState() == GameState.RUNNING) {
            sender.sendMessage(colorize("&c游戏正在进行中，无法删除"));
            return true;
        }

        if (gameManager.removeGame(gameName)) {
            sender.sendMessage(getColoredMessage("&a成功删除游戏房间 &e%s", gameName));
            return true;
        }

        sender.sendMessage(colorize("&c删除游戏房间失败"));
        return false;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }
}
