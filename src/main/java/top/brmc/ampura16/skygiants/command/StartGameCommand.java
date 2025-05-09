package top.brmc.ampura16.skygiants.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.brmc.ampura16.skygiants.game.Game;
import top.brmc.ampura16.skygiants.game.GameManager;
import top.brmc.ampura16.skygiants.game.GameState;
import top.brmc.ampura16.skygiants.utils.IColorizable;

import java.util.List;

public class StartGameCommand implements SubCommand, IColorizable {
    private final GameManager gameManager;
    private final String commandPrefix;

    public StartGameCommand(GameManager gameManager, String commandPrefix) {
        this.gameManager = gameManager;
        this.commandPrefix = commandPrefix;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        // 权限检查
        if (!sender.hasPermission("skygiants.admin")) {
            sender.sendMessage(colorize("&c你没有权限强制开始游戏"));
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

        // 检查游戏状态
        if (game.getState() != GameState.WAITING) {
            sender.sendMessage(getColoredMessage("&c游戏 &e%s &c当前状态无法开始 (当前状态: %s)",
                    game.getTeamName(), game.getState().name()));
            return true;
        }

        // 检查玩家数量
        if (game.canStart()) {
            sender.sendMessage(getColoredMessage(
                    "&c游戏 &e%s &c需要至少 &e%d &c名玩家才能开始 (当前: %d/%d)",
                    game.getTeamName(), game.getMinPlayers(),
                    game.getPlayerCount(), game.getMaxPlayers()));
            return true;
        }

        // 开始游戏
        if (game.start()) {
            // 通知所有玩家
            game.getPlayers().forEach(player ->
                    player.sendMessage(colorize("&a游戏已经开始!")));

            sender.sendMessage(getColoredMessage("&a成功开始游戏 &e%s", game.getTeamName()));
            return true;
        }

        sender.sendMessage(getColoredMessage("&c无法开始游戏 &e%s", game.getTeamName()));
        return false;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }
}
