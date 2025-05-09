package top.brmc.ampura16.skygiants.scoreboard;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import top.brmc.ampura16.skygiants.game.Game;
import top.brmc.ampura16.skygiants.game.GameManager;
import top.brmc.ampura16.skygiants.game.Team;
import top.brmc.ampura16.skygiants.utils.IColorizable;

import java.util.List;

public class ScoreboardManager implements IColorizable {

    private final GameManager gameManager;
    private final FileConfiguration config;

    public ScoreboardManager(GameManager gameManager, FileConfiguration config) {
        this.gameManager = gameManager;
        this.config = config;
    }

    /**
     * 更新大厅计分板
     *
     * @param game 游戏对象
     */
    public void updateWaitingScoreboard(Game game) {
        // 获取配置中的计分板标题和内容
        String title = config.getString("scoreboards.waiting.title", "&bSkyGiants");
        List<String> content = config.getStringList("scoreboards.waiting.content");

        // 为每个玩家单独设置计分板
        for (Player player : game.getPlayers()) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = scoreboard.registerNewObjective("waiting", "dummy");

            // 设置计分板标题
            objective.setDisplayName(colorize(title));
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            // 添加计分板内容
            int rowNumber = content.size();
            for (String row : content) {
                if (row.trim().isEmpty()) {
                    row = "§r";
                }

                // 解析 PAPI 占位符，传递当前玩家对象
                row = PlaceholderAPI.setPlaceholders(player, row);

                // 使用 IColorizable 接口处理颜色符号
                row = colorize(row);

                // 截断条目，确保长度不超过 40 个字符
                if (row.length() > 40) {
                    row = row.substring(0, 40);
                }

                Score score = objective.getScore(row);
                score.setScore(rowNumber);
                rowNumber--;
            }

            // 为当前玩家设置计分板
            player.setScoreboard(scoreboard);
        }
    }

    /**
     * 更新队伍选择后的计分板
     *
     * @param game 游戏对象
     * @param team 队伍对象
     */
    public void updateTeamSelectedScoreboard(Game game, Team team) {
        // 获取配置中的计分板标题和内容
        String title = config.getString("scoreboards.team-selected.title", "&bSkyGiants");
        List<String> content = config.getStringList("scoreboards.team-selected.content");

        // 为每个玩家单独设置计分板
        for (Player player : team.getPlayers()) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = scoreboard.registerNewObjective("team", "dummy");

            // 设置计分板标题
            objective.setDisplayName(colorize(title));
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            // 添加计分板内容
            int rowNumber = content.size();
            for (String row : content) {
                if (row.trim().isEmpty()) {
                    row = "§r";
                }

                // 解析 PAPI 占位符，传递当前玩家对象
                row = PlaceholderAPI.setPlaceholders(player, row);

                // 使用 IColorizable 接口处理颜色符号
                row = colorize(row);

                // 截断条目，确保长度不超过 40 个字符
                if (row.length() > 40) {
                    row = row.substring(0, 40);
                }

                Score score = objective.getScore(row);
                score.setScore(rowNumber);
                rowNumber--;
            }

            // 为当前玩家设置计分板
            player.setScoreboard(scoreboard);
        }
    }

    /**
     * 更新玩家的计分板为 ingame 格式
     *
     * @param game   游戏对象
     * @param player 玩家对象
     */
    /**
     * 更新玩家的计分板为 ingame 格式
     *
     * @param game   游戏对象
     * @param player 玩家对象
     */
    public void updateIngameScoreboard(Game game, Player player) {
        // 获取配置中的计分板标题和内容
        String title = config.getString("scoreboards.ingame.title", "&bSkyGiants");
        List<String> content = config.getStringList("scoreboards.ingame.content");

        // 创建新的计分板
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("ingame", "dummy");

        // 设置计分板标题
        objective.setDisplayName(colorize(title));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // 添加计分板内容
        int rowNumber = content.size();
        for (String row : content) {
            if (row.trim().isEmpty()) {
                row = "§r";
            }

            // 解析 PAPI 占位符，传递当前玩家对象
            row = PlaceholderAPI.setPlaceholders(player, row);

            // 使用 IColorizable 接口处理颜色符号
            row = colorize(row);

            // 截断条目，确保长度不超过 40 个字符
            if (row.length() > 40) {
                row = row.substring(0, 40);
            }

            Score score = objective.getScore(row);
            score.setScore(rowNumber);
            rowNumber--;
        }

        // 为当前玩家设置计分板
        player.setScoreboard(scoreboard);
    }

    /**
     * 重置玩家的计分板为默认计分板
     *
     * @param player 玩家对象
     */
    public void resetScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        return false;
    }
}
