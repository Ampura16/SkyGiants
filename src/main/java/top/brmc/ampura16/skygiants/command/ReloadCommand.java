package top.brmc.ampura16.skygiants.command;

import org.bukkit.command.CommandSender;
import top.brmc.ampura16.skygiants.Main;
import top.brmc.ampura16.skygiants.config.DefaultConfigManager;
import top.brmc.ampura16.skygiants.game.GameManager;
import top.brmc.ampura16.skygiants.utils.IColorizable;

import java.util.List;

public class ReloadCommand implements SubCommand, IColorizable {
    private final Main plugin;
    private final GameManager gameManager;
    private final DefaultConfigManager configManager;

    public ReloadCommand(Main plugin, GameManager gameManager, DefaultConfigManager configManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.configManager = configManager;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if (!sender.hasPermission("skygiants.admin")) {
            sender.sendMessage(colorize("&c你没有权限执行此命令"));
            return true;
        }

        String type = args.isEmpty() ? "all" : args.get(0).toLowerCase();

        switch (type) {
            case "all":
                reloadAll(sender);
                break;
            case "config":
                reloadConfig(sender);
                break;
            case "games":
                reloadGames(sender);
                break;
            default:
                sender.sendMessage(colorize("&c无效的重载类型，可用选项: all, config, games"));
                return false;
        }

        sender.sendMessage(colorize("&a配置重载完成!"));
        return true;
    }

    private void reloadAll(CommandSender sender) {
        sender.sendMessage(colorize("&e开始重载所有配置..."));
        reloadConfig(sender);
        reloadGames(sender);
    }

    private void reloadConfig(CommandSender sender) {
        sender.sendMessage(colorize("&e重载主配置文件..."));
        configManager.reloadConfig();

        // 更新游戏管理器中的默认玩家限制
        int minPlayers = configManager.getConfig().getInt("default-game.min-players", 2);
        int maxPlayers = configManager.getConfig().getInt("default-game.max-players", 8);
        gameManager.updateDefaultPlayerLimits(minPlayers, maxPlayers);
    }

    private void reloadGames(CommandSender sender) {
        sender.sendMessage(colorize("&e重载所有游戏..."));
        gameManager.reloadAllGames();
    }

    public Main getPlugin() {
        return plugin;
    }
}
