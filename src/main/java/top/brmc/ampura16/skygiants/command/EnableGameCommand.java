package top.brmc.ampura16.skygiants.command;

import org.bukkit.command.CommandSender;
import top.brmc.ampura16.skygiants.game.Game;
import top.brmc.ampura16.skygiants.game.GameManager;
import top.brmc.ampura16.skygiants.game.GameState;
import top.brmc.ampura16.skygiants.utils.IColorizable;

import java.util.List;

public class EnableGameCommand implements SubCommand, IColorizable {
    private final GameManager gameManager;
    private final String commandPrefix;

    public EnableGameCommand(GameManager gameManager, String commandPrefix) {
        this.gameManager = gameManager;
        this.commandPrefix = commandPrefix;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        // 权限检查
        if (!sender.hasPermission("skygiants.admin")) {
            sender.sendMessage(colorize("&c你没有权限启用游戏"));
            return true;
        }

        // 参数检查
        if (args.isEmpty()) {
            sender.sendMessage(colorize("&c用法: " + getCommandPrefix() + " &6create <房间名称>"));
            return true;
        }

        String gameName = args.get(0);
        Game game = gameManager.getGame(gameName);

        // 游戏存在性检查
        if (game == null) {
            sender.sendMessage(getColoredMessage("&c游戏 &e%s &c不存在", gameName));
            return true;
        }

        // 检查游戏是否已经处于等待状态
        if (game.getState() == GameState.WAITING) {
            sender.sendMessage(getColoredMessage("&c游戏 &e%s &c已经处于等待状态", gameName));
            return true;
        }

        // 检查游戏是否正在运行
        if (game.getState() == GameState.RUNNING) {
            sender.sendMessage(getColoredMessage("&c游戏 &e%s &c正在运行中，请先停止游戏", gameName));
            return true;
        }

        // 启用游戏（将状态从STOPPED改为WAITING）
        game.setState(GameState.WAITING);
        gameManager.saveGame(game); // 保存状态变更

        sender.sendMessage(getColoredMessage("&a成功启用游戏 &e%s", gameName));
        return true;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }
}
