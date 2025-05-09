package top.brmc.ampura16.skygiants.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.brmc.ampura16.skygiants.game.Game;
import top.brmc.ampura16.skygiants.game.GameManager;
import top.brmc.ampura16.skygiants.game.GameState;
import top.brmc.ampura16.skygiants.utils.IColorizable;

import java.util.List;

public class StopGameCommand implements SubCommand, IColorizable {
    private final GameManager gameManager;
    private final String commandPrefix;

    public StopGameCommand(GameManager gameManager, String commandPrefix) {
        this.gameManager = gameManager;
        this.commandPrefix = commandPrefix;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        // 权限检查
        if (!sender.hasPermission("SkyGiants.admin")) {
            sender.sendMessage(colorize("&c你没有权限停止游戏"));
            return true;
        }

        Game game;

        // 如果没有指定游戏名称，尝试获取玩家所在的游戏
        if (args.isEmpty()) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(colorize("&c用法: " + getCommandPrefix() + " &6stop <房间名称>"));
                return true;
            }

            Player player = (Player) sender;
            game = gameManager.getPlayerGame(player);

            if (game == null) {
                sender.sendMessage(colorize("&c你不在任何游戏中"));
                return true;
            }
        } else {
            // 通过名称获取游戏
            String gameName = args.get(0);
            game = gameManager.getGame(gameName);

            if (game == null) {
                sender.sendMessage(getColoredMessage("&c游戏 &e%s &c不存在", gameName));
                return true;
            }
        }

        // 检查游戏是否已经停止
        if (game.getState() == GameState.STOPPED) {
            sender.sendMessage(getColoredMessage("&c游戏 &e%s &c已经处于停止状态", game.getName()));
            return true;
        }

        // 停止游戏
        game.stop();
        gameManager.saveGame(game); // 保存状态变更

        // 通知所有玩家
        game.getPlayers().forEach(player ->
                player.sendMessage(colorize("&a游戏已被管理员停止")));

        sender.sendMessage(getColoredMessage("&a成功停止游戏 &e%s", game.getName()));
        return true;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }
}
