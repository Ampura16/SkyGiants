package top.brmc.ampura16.skygiants.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.brmc.ampura16.skygiants.game.Game;
import top.brmc.ampura16.skygiants.game.GameManager;
import top.brmc.ampura16.skygiants.game.Team;
import top.brmc.ampura16.skygiants.utils.IColorizable;

import java.util.List;

public class SetTeamSpawnCommand implements SubCommand, IColorizable {
    private final GameManager gameManager;
    private final String commandPrefix;

    public SetTeamSpawnCommand(GameManager gameManager, String commandPrefix) {
        this.gameManager = gameManager;
        this.commandPrefix = commandPrefix;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(colorize("&c只有玩家可以使用此命令"));
            return false;
        }

        // 检查参数数量
        if (args.size() < 2) {
            sender.sendMessage(colorize("&c用法: " + commandPrefix + " team setspawn <房间名称> <队伍名称>"));
            return false;
        }

        Player player = (Player) sender;

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

        // 验证游戏是否正在运行
        if (game.isRunning()) {
            sender.sendMessage(colorize("&c游戏正在进行中,无法设置出生点" + gameName));
            return false;
        }

        // 验证队伍是否存在
        Team team = game.getTeam(teamName);
        if (team == null) {
            sender.sendMessage(colorize("&c队伍不存在: " + teamName));
            return false;
        }

        // 设置队伍出生点
        team.setTeamSpawnLocation(player.getLocation());
        sender.sendMessage(colorize("&a成功设置队伍出生点: " + teamName));

        // 保存队伍出生点到配置文件
        if (game.saveTeamSpawnToConfig(teamName, player.getLocation())) {
            sender.sendMessage(colorize("&a队伍出生点已保存到配置文件: " + teamName));
        } else {
            sender.sendMessage(colorize("&c保存队伍出生点到配置文件失败: " + teamName));
        }

        return true;
    }

    @Override
    public String getPermission() {
        return "skygiants.admin";
    }
}
