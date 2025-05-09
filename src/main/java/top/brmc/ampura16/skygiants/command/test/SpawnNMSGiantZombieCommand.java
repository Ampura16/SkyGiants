package top.brmc.ampura16.skygiants.command.test;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.brmc.ampura16.skygiants.command.SubCommand;
import top.brmc.ampura16.skygiants.nms.v1_8_R3.NMSEntityManager;
import top.brmc.ampura16.skygiants.utils.IColorizable;

import java.util.List;

public class SpawnNMSGiantZombieCommand implements SubCommand, IColorizable {
    private final NMSEntityManager nmsEntityManager;

    public SpawnNMSGiantZombieCommand(NMSEntityManager nmsEntityManager) {
        this.nmsEntityManager = nmsEntityManager;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        // 检查发送者是否为玩家
        if (!isPlayer(sender)) {
            sender.sendMessage(colorize("&c只有玩家可以执行此命令."));
            return true;
        }

        Player player = (Player) sender;
        Location location = player.getLocation();

        // 生成巨人僵尸
        if (spawnGiantZombie(location)) {
            player.sendMessage(colorize("&aNMS巨人僵尸已生成."));
        } else {
            player.sendMessage(colorize("&c生成失败，请检查生成位置是否有效."));
        }

        return true;
    }

    // 检查发送者是否为玩家
    private boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }

    // 生成巨人僵尸
    private boolean spawnGiantZombie(Location location) {
        try {
            // 调用 NMSEntityManager 生成巨人僵尸
            return nmsEntityManager.createGiantZombie(location);
        } catch (IllegalArgumentException e) {
            // 捕获无效位置的异常
            System.out.println("生成失败: " + e.getMessage());
            return false;
        }
    }
}
