package top.brmc.ampura16.skygiants.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import top.brmc.ampura16.skygiants.game.Game;
import top.brmc.ampura16.skygiants.game.GameManager;
import top.brmc.ampura16.skygiants.game.TeamColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SkyGiantsTabCompleter implements TabCompleter {
    private final GameManager gameManager;

    // 所有可用的子命令列表
    private static final List<String> SUB_COMMANDS = Arrays.asList(
            "help", "create", "remove", "join", "leave", "list",
            "setlobby", "start", "stop", "enable", "reload", "team", "spawngiantzombie", "getgamesetuptools");

    // reload 子命令的选项
    private static final List<String> RELOAD_OPTIONS = Arrays.asList(
            "all", "config", "games");

    // 需要游戏名称作为参数的子命令
    private static final List<String> GAME_NAME_COMMANDS = Arrays.asList(
            "join", "remove", "setlobby", "start", "stop", "enable");

    // team 子命令的子命令
    private static final List<String> TEAM_SUB_COMMANDS = Arrays.asList(
            "add", "remove", "setspawn", "setteamgiantspawn");

    public SkyGiantsTabCompleter(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        final List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // 第一级补全 - 子命令名称
            StringUtil.copyPartialMatches(args[0], SUB_COMMANDS, completions);
        } else if (args.length == 2) {
            // 第二级补全 - 根据子命令提供不同的补全
            String subCommand = args[0].toLowerCase();

            if (GAME_NAME_COMMANDS.contains(subCommand)) {
                // 需要游戏名称的子命令
                List<String> gameNames = gameManager.getAllGameNames();
                StringUtil.copyPartialMatches(args[1], gameNames, completions);
            } else if ("reload".equals(subCommand)) {
                // reload 子命令的选项
                StringUtil.copyPartialMatches(args[1], RELOAD_OPTIONS, completions);
            } else if ("team".equals(subCommand)) {
                // team 子命令的子命令
                StringUtil.copyPartialMatches(args[1], TEAM_SUB_COMMANDS, completions);
            }
        } else if (args.length == 3 && "team".equals(args[0].toLowerCase())) {
            // team 子命令的第三个参数补全
            String teamSubCommand = args[1].toLowerCase();
            if ("add".equals(teamSubCommand) || "remove".equals(teamSubCommand) ||
                    "setspawn".equals(teamSubCommand) || "setteamgiantspawn".equals(teamSubCommand)) {
                // team add, remove, setspawn 或 setteamgiantspawn 命令的第三个参数补全 (房间名称)
                List<String> gameNames = gameManager.getAllGameNames();
                StringUtil.copyPartialMatches(args[2], gameNames, completions);
            }
        } else if (args.length == 4 && "team".equals(args[0].toLowerCase())) {
            // team 子命令的第四个参数补全
            String teamSubCommand = args[1].toLowerCase();
            if ("add".equals(teamSubCommand)) {
                // team add 命令的第四个参数补全 (队伍名称)
                completions.add("请输入队伍名称");
            } else if ("remove".equals(teamSubCommand) || "setspawn".equals(teamSubCommand) ||
                    "setteamgiantspawn".equals(teamSubCommand)) {
                // team remove, setspawn 或 setteamgiantspawn 命令的第四个参数补全 (队伍名称)
                String gameName = args[2];
                Game game = gameManager.getGame(gameName);
                if (game != null) {
                    List<String> teamNames = new ArrayList<>(game.getTeams().keySet());
                    StringUtil.copyPartialMatches(args[3], teamNames, completions);
                }
            }
        } else if (args.length == 5 && "team".equals(args[0].toLowerCase()) && "add".equals(args[1].toLowerCase())) {
            // team add 命令的第五个参数补全 (队伍颜色)
            List<String> colors = new ArrayList<>();
            for (TeamColor color : TeamColor.values()) {
                colors.add(color.name().toLowerCase());
            }
            StringUtil.copyPartialMatches(args[4], colors, completions);
        } else if (args.length == 6 && "team".equals(args[0].toLowerCase()) && "add".equals(args[1].toLowerCase())) {
            // team add 命令的第六个参数补全 (队伍最大人数)
            List<String> maxPlayers = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
            StringUtil.copyPartialMatches(args[5], maxPlayers, completions);
        }

        Collections.sort(completions);
        return completions;
    }
}
