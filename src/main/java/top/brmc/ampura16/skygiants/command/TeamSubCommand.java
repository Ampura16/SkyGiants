package top.brmc.ampura16.skygiants.command;

import org.bukkit.command.CommandSender;
import top.brmc.ampura16.skygiants.game.GameManager;
import top.brmc.ampura16.skygiants.utils.IColorizable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamSubCommand implements SubCommand, IColorizable {
    private final GameManager gameManager;
    private final String commandPrefix;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public TeamSubCommand(GameManager gameManager, String commandPrefix) {
        this.gameManager = gameManager;
        this.commandPrefix = commandPrefix;

        // 注册 team 子命令的子命令
        registerSubCommand("add", new AddTeamCommand(gameManager, commandPrefix));
        registerSubCommand("remove", new RemoveTeamCommand(gameManager, commandPrefix));
        registerSubCommand("setspawn", new SetTeamSpawnCommand(gameManager, commandPrefix));
    }

    private void registerSubCommand(String name, SubCommand command) {
        subCommands.put(name.toLowerCase(), command);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if (args.isEmpty()) {
            sender.sendMessage(colorize("&c用法: " + commandPrefix + " team <add|remove|setspawn> ..."));
            sender.sendMessage(colorize("&6详情查看/sgs help"));
            return false;
        }

        String subCommandName = args.get(0).toLowerCase();
        SubCommand subCommand = subCommands.get(subCommandName);

        if (subCommand == null) {
            sender.sendMessage(colorize("&c未知子命令,使用 " + commandPrefix + " team help 查看帮助"));
            return false;
        }

        List<String> subArgs = args.subList(1, args.size());
        return subCommand.execute(sender, subArgs);
    }

    @Override
    public String getPermission() {
        return "skygiants.admin";
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
