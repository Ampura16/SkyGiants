package top.brmc.ampura16.skygiants.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.brmc.ampura16.skygiants.Main;
import top.brmc.ampura16.skygiants.command.test.SpawnNMSGiantZombieCommand;
import top.brmc.ampura16.skygiants.config.DefaultConfigManager;
import top.brmc.ampura16.skygiants.game.GameManager;
import top.brmc.ampura16.skygiants.utils.IColorizable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkyGiantsCommand implements CommandExecutor, IColorizable {
    private final GameManager gameManager;
    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private final Main plugin;
    private final String commandPrefix;

    public SkyGiantsCommand(Main plugin, GameManager gameManager, DefaultConfigManager configManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.commandPrefix = getCommandName(); // 初始化命令前缀

        // 注册所有子命令
        registerSubCommand("help", new HelpCommand(this));
        registerSubCommand("create", new CreateSubCommand(gameManager, commandPrefix));
        registerSubCommand("remove", new RemoveSubCommand(gameManager, commandPrefix));
        registerSubCommand("join", new JoinSubCommand(gameManager, commandPrefix));
        registerSubCommand("leave", new LeaveSubCommand(gameManager));
        registerSubCommand("list", new ListSubCommand(gameManager));
        registerSubCommand("setlobby", new SetLobbyCommand(gameManager, commandPrefix));
        registerSubCommand("stop", new StopGameCommand(gameManager, commandPrefix));
        registerSubCommand("start", new StartGameCommand(gameManager, commandPrefix));
        registerSubCommand("enable", new EnableGameCommand(gameManager, commandPrefix));
        registerSubCommand("spawngiantzombie", new SpawnNMSGiantZombieCommand(plugin.getNMSEntityManager()));
        registerSubCommand("getgamesetuptools", new GetGameSetupToolsCommand());
        registerSubCommand("reload", new ReloadCommand(plugin, gameManager, configManager));

        // 注册 team 子命令
        TeamSubCommand teamCommand = new TeamSubCommand(gameManager, commandPrefix);
        registerSubCommand("team", teamCommand);
    }

    private void registerSubCommand(String name, SubCommand command) {
        subCommands.put(name.toLowerCase(), command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(colorize("&c只有玩家可以使用此命令"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommandName = args[0].toLowerCase();
        SubCommand subCommand = subCommands.get(subCommandName);

        if (subCommand == null) {
            sender.sendMessage(colorize(String.format("&c未知子命令，使用 %s help 查看帮助", commandPrefix)));
            return true;
        }

        List<String> subArgs = Arrays.asList(args).subList(1, args.length);
        return subCommand.execute(sender, subArgs);
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage(colorize("&a========== &bSkyGiants 帮助 &a=========="));
        sender.sendMessage(colorize(commandPrefix + " &a[help] &f- 打印指令帮助"));
        sender.sendMessage(colorize(commandPrefix + " &ajoin <名称> &f- 加入游戏房间"));
        sender.sendMessage(colorize(commandPrefix + " &aleave &f- 离开当前游戏"));
        sender.sendMessage(colorize(commandPrefix + " &alist &f- 列出所有游戏房间"));
        sender.sendMessage(colorize(commandPrefix + " &6create <名称> &f- 创建游戏房间"));
        sender.sendMessage(colorize(commandPrefix + " &6remove <名称> &f- 删除游戏房间"));
        sender.sendMessage(colorize(commandPrefix + " &6team add <房间名称> <队伍名称> <队伍颜色> <队伍最大人数> &f- 添加队伍"));
        sender.sendMessage(colorize(commandPrefix + " &6team remove <房间名称> <队伍名称> &f- 删除队伍"));
        sender.sendMessage(colorize(commandPrefix + " &6team setspawn <房间名称> <队伍名称> &f- 设置队伍出生点"));
        sender.sendMessage(colorize(commandPrefix + " &6team setgiantspawn <房间名称> <队伍名称> &f- 设置队伍巨人生成点"));
        sender.sendMessage(colorize(commandPrefix + " &6setlobby <名称> &f- 设置游戏房间大厅"));
        sender.sendMessage(colorize(commandPrefix + " &6stop <名称> &f- 停止正在进行的游戏"));
        sender.sendMessage(colorize(commandPrefix + " &6start <名称> &f- 开始等待中的游戏"));
        sender.sendMessage(colorize(commandPrefix + " &6enable <名称> &f- 启用已停止的游戏"));
        sender.sendMessage(colorize(commandPrefix + " &espawngiantzombie &f- 生成NMS巨人僵尸"));
        sender.sendMessage(colorize(commandPrefix + " &egetgamesetuptools &f- 获取地图设置工具"));
        sender.sendMessage(colorize(commandPrefix + " &dreload <all|config|games> &f- 重载<全部|配置|游戏>配置"));
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public Main getPlugin() {
        return plugin;
    }

    public String getCommandName() {
        return colorize("&b/skygiants");
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        return false;
    }
}

