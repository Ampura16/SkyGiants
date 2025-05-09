package top.brmc.ampura16.skygiants.command;

import org.bukkit.command.CommandSender;
import top.brmc.ampura16.skygiants.game.Game;
import top.brmc.ampura16.skygiants.game.GameManager;
import top.brmc.ampura16.skygiants.utils.IColorizable;

import java.util.List;

public class RemoveTeamCommand implements SubCommand, IColorizable {
    private final GameManager gameManager;
    private final String commandPrefix;

    public RemoveTeamCommand(GameManager gameManager, String commandPrefix) {
        this.gameManager = gameManager;
        this.commandPrefix = commandPrefix;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        // 检查参数数量
        if (args.size() < 2) {
            sender.sendMessage(colorize("&c用法: " + commandPrefix + " team remove <房间名称> <队伍名称>"));
            return false;
        }

        // 获取参数
        String gameName = args.get(0); // 房间名称
        String teamName = args.get(1); // 队伍名称

        // 检查房间名称是否为空
        if (gameName == null || gameName.trim().isEmpty()) {
            sender.sendMessage(colorize("&c房间名称不能为空"));
            return false;
        }

        // 检查队伍名称是否为空
        if (teamName == null || teamName.trim().isEmpty()) {
            sender.sendMessage(colorize("&c队伍名称不能为空"));
            return false;
        }

        // 验证房间是否存在
        Game game = gameManager.getGame(gameName);
        if (game == null) {
            sender.sendMessage(colorize("&c房间不存在: " + gameName));
            return false;
        }

        // 验证队伍是否存在
        if (game.getTeam(teamName) == null) {
            sender.sendMessage(colorize("&c队伍不存在: " + teamName));
            return false;
        }

        // 删除队伍
        if (game.removeTeam(teamName)) {
            // 从配置文件中删除队伍信息
            if (game.removeTeamFromConfig(teamName)) {
                sender.sendMessage(colorize("&a成功删除队伍并同步配置文件: " + teamName));
            } else {
                sender.sendMessage(colorize("&c删除队伍成功,但同步配置文件失败"));
            }
        } else {
            sender.sendMessage(colorize("&c删除队伍失败,请检查日志."));
        }
        return true;
    }

    @Override
    public String getPermission() {
        return "skygiants.admin";
    }
}
