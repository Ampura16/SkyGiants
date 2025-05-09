package top.brmc.ampura16.skygiants.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.brmc.ampura16.skygiants.game.Game;
import top.brmc.ampura16.skygiants.game.GameManager;
import top.brmc.ampura16.skygiants.game.Team;

public class SkyGiantsPlaceholder extends PlaceholderExpansion {
    private final GameManager gameManager;

    public SkyGiantsPlaceholder(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "SkyGiants";
    }

    @Override
    public @NotNull String getAuthor() {
        return "brmc";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return null; // 如果玩家为空，返回 null
        }

        // 获取玩家所在的游戏
        Game game = gameManager.getPlayerGame(player);
        if (game == null) {
            return "无"; // 如果玩家不在任何游戏中，返回默认值
        }

        // 根据占位符参数返回相应的值
        switch (params.toLowerCase()) {
            case "currentmapname": // 与 config.yml 中的占位符一致
                return game.getTeamName(); // 地图名称
            case "currentgamestats":
                return game.getState().getDisplayName(); // 当前地图状态
            case "currentplayers": // 与 config.yml 中的占位符一致
                return String.valueOf(game.getPlayerCount()); // 当前地图玩家数
            case "maxplayers": // 与 config.yml 中的占位符一致
                return String.valueOf(game.getMaxPlayers()); // 当前地图最大玩家数
            case "currentteam": // 与 config.yml 中的占位符一致
                Team team = game.getPlayerTeam(player);
                return team != null ? team.getTeamName() : "无队伍";
            case "currentmapgamestats": // 当前地图状态
                return game.getState().getDisplayName();
            default:
                return null; // 未知占位符返回 null
        }
    }
}
