package top.brmc.ampura16.skygiants.game;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class StartCountdownTask extends BukkitRunnable {
    private final Game game;
    private int timeLeft;

    public StartCountdownTask(Game game, int timeLeft) {
        this.game = game;
        this.timeLeft = timeLeft;
    }

    @Override
    public void run() {
        if (timeLeft <= 0) {
            // 倒计时结束，启动游戏
            game.setState(GameState.RUNNING);
            game.sendMessageToAllPlayers(ChatColor.GREEN + "游戏开始.");
            game.setCountdownTask(null); // 清除倒计时任务
            this.cancel();
            return;
        }

        // 向房间内的玩家发送剩余时间
        game.sendMessageToAllPlayers(ChatColor.YELLOW + "游戏将在 " + timeLeft + " 秒后开始...");
        timeLeft--;
    }
}
