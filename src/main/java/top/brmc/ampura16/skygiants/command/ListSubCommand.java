package top.brmc.ampura16.skygiants.command;

import org.bukkit.command.CommandSender;
import top.brmc.ampura16.skygiants.game.Game;
import top.brmc.ampura16.skygiants.game.GameManager;
import top.brmc.ampura16.skygiants.game.GameState;
import java.util.List;

public class ListSubCommand implements SubCommand {
    private final GameManager gameManager;

    public ListSubCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        List<String> gameNames = gameManager.getAllGameNames();

        if (gameNames.isEmpty()) {
            sender.sendMessage(colorize("&a===== &e游戏房间列表 &a====="));
            sender.sendMessage(colorize("&c当前没有可用的游戏房间"));
            return true;
        }

        sender.sendMessage(colorize("&a===== &e游戏房间列表 &a====="));
        for (String name : gameNames) {
            Game game = gameManager.getGame(name);
            String state = getStateDisplay(game.getState());
            sender.sendMessage(getColoredMessage("&b%s &f- 状态: %s &f- 玩家: %d/%d",
                    name, state, game.getPlayerCount(), game.getMaxPlayers()));
        }
        return true;
    }

    private String getStateDisplay(GameState state) {
        switch (state) {
            case WAITING:
                return "&a等待中";
            case RUNNING:
                return "&6进行中";
            case STOPPED:
                return "&c已停止";
            default:
                return "&7未知";
        }
    }
}
