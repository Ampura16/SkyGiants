package top.brmc.ampura16.skygiants.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.brmc.ampura16.skygiants.game.Game;
import top.brmc.ampura16.skygiants.game.GameManager;
import top.brmc.ampura16.skygiants.game.GameState;
import java.util.List;

public class JoinSubCommand implements SubCommand {
    private final GameManager gameManager;
    private final String commandPrefix;

    public JoinSubCommand(GameManager gameManager, String commandPrefix) {
        this.gameManager = gameManager;
        this.commandPrefix = commandPrefix;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(colorize("&c只有玩家可以加入游戏房间"));
            return true;
        }

        if (args.isEmpty()) {
            sender.sendMessage(colorize("&c用法: " + getCommandPrefix() + " &ajoin <房间名称>"));
            return true;
        }

        String gameName = args.get(0);
        Player player = (Player) sender;

        Game currentGame = gameManager.getPlayerGame(player);
        if (currentGame != null) {
            if (currentGame.getState() == GameState.RUNNING) {
                sender.sendMessage(colorize("&c你已经在游戏中，无法加入其他房间"));
                return true;
            }
            gameManager.leaveGame(player);
        }

        Game targetGame = gameManager.getGame(gameName);
        if (targetGame == null) {
            sender.sendMessage(getColoredMessage("&c游戏房间 &e%s &c不存在", gameName));
            return true;
        }

        if (targetGame.getState() != GameState.WAITING) {
            sender.sendMessage(colorize("&c该游戏已经开始或处于停止状态,无法加入."));
            return true;
        }

        if (gameManager.joinGame(player, gameName)) {
            sender.sendMessage(getColoredMessage("&a成功加入游戏房间 &e%s", gameName));
            return true;
        }

        sender.sendMessage(colorize("&c加入游戏房间失败"));
        return false;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }
}
