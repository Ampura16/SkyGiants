package top.brmc.ampura16.skygiants.command;

import org.bukkit.command.CommandSender;
import top.brmc.ampura16.skygiants.game.Game;
import top.brmc.ampura16.skygiants.game.GameManager;
import top.brmc.ampura16.skygiants.game.TeamColor;
import top.brmc.ampura16.skygiants.utils.IColorizable;

import java.util.List;

public class AddTeamCommand implements SubCommand, IColorizable {
    private final GameManager gameManager;
    private final String commandPrefix;

    public AddTeamCommand(GameManager gameManager, String commandPrefix) {
        this.gameManager = gameManager;
        this.commandPrefix = commandPrefix;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        // 打印参数列表以调试
        sender.sendMessage(colorize("&a参数列表: " + args));

        // 检查参数数量
        if (args.size() < 4) {
            sender.sendMessage(colorize("&c用法: " + commandPrefix + " team add <房间名称> <队伍名称> <队伍颜色> <队伍最大人数>"));
            return false;
        }

        // 获取参数
        String gameName = args.get(0); // 房间名称
        String teamName = args.get(1); // 队伍名称
        String colorName = args.get(2); // 队伍颜色
        String maxPlayersStr = args.get(3); // 队伍最大人数

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

        // 检查队伍颜色是否为空
        if (colorName == null || colorName.trim().isEmpty()) {
            sender.sendMessage(colorize("&c队伍颜色不能为空"));
            return false;
        }

        // 检查队伍最大人数是否为空
        if (maxPlayersStr == null || maxPlayersStr.trim().isEmpty()) {
            sender.sendMessage(colorize("&c队伍最大人数不能为空"));
            return false;
        }

        // 验证队伍颜色
        TeamColor color = TeamColor.fromName(colorName);
        if (color == null) {
            sender.sendMessage(colorize("&c无效的颜色: " + colorName));
            return false;
        }

        // 验证队伍最大人数
        int maxPlayers;
        try {
            maxPlayers = Integer.parseInt(maxPlayersStr);
        } catch (NumberFormatException e) {
            sender.sendMessage(colorize("&c队伍最大人数必须是整数"));
            return false;
        }

        if (maxPlayers < 1 || maxPlayers > 24) {
            sender.sendMessage(colorize("&c队伍最大人数必须在 1 到 24 之间"));
            return false;
        }

        // 验证房间是否存在
        Game game = gameManager.getGame(gameName);
        if (game == null) {
            sender.sendMessage(colorize("&c房间不存在: " + gameName));
            return false;
        }

        // 验证队伍名称
        if (teamName.length() < 3 || teamName.length() > 20) {
            sender.sendMessage(colorize("&c队伍名称长度必须在 3 到 20 个字符之间"));
            return false;
        }

        // 检查队伍是否已存在
        if (game.getTeam(teamName) != null) {
            sender.sendMessage(colorize("&c队伍名称已被使用: " + teamName));
            return false;
        }

        // 创建队伍
        if (game.createTeam(teamName, color, maxPlayers)) {
            // 保存队伍信息到配置文件
            if (game.saveTeamToConfig(teamName, color, maxPlayers)) {
                sender.sendMessage(colorize("&a队伍创建成功并保存到配置文件: " + teamName));
            } else {
                sender.sendMessage(colorize("&c队伍创建成功,但保存到配置文件失败"));
            }
        } else {
            sender.sendMessage(colorize("&c队伍创建失败"));
        }

        return true;
    }

    @Override
    public String getPermission() {
        return "skygiants.admin";
    }
}
