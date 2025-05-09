package top.brmc.ampura16.skygiants.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.brmc.ampura16.skygiants.game.Game;
import top.brmc.ampura16.skygiants.game.GameManager;
import top.brmc.ampura16.skygiants.game.GameState;
import top.brmc.ampura16.skygiants.utils.IColorizable;

import java.util.List;

public class SetLobbyCommand implements SubCommand, IColorizable {
    private final GameManager gameManager;
    private final String commandPrefix;

    public SetLobbyCommand(GameManager gameManager, String commandPrefix) {
        this.gameManager = gameManager;
        this.commandPrefix = commandPrefix;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        // 权限检查
        if (!sender.hasPermission("skygiants.admin")) {
            sender.sendMessage(colorize("&c你没有权限停止游戏"));
            return true;
        }

        if (args.isEmpty()) {
            sender.sendMessage(colorize("&c用法: " + getCommandPrefix() + " &6setlobby <房间名称>"));
            return true;
        }

        Player player = (Player) sender;
        String gameName = args.get(0);
        Game game = gameManager.getGame(gameName);

        if (game == null) {
            sender.sendMessage(getColoredMessage("&c游戏房间 &e%s &c不存在", gameName));
            return true;
        }

        if (game.getState() != GameState.STOPPED) {
            sender.sendMessage(colorize("&c游戏没有处于关闭状态,无法设置大厅."));
            return true;
        }

        game.setLobby(player.getLocation());
        gameManager.saveGame(game); // 保存修改后的配置
        sender.sendMessage(getColoredMessage("&a成功设置游戏 &e%s &a的大厅位置", gameName));
        return true;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }
}
