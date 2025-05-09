package top.brmc.ampura16.skygiants.game;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;

/**
 * 队伍颜色枚举类，用于定义队伍的颜色、聊天颜色和染料颜色。
 */
public enum TeamColor {
    GREEN(Color.fromRGB(85, 255, 85), ChatColor.GREEN, DyeColor.LIME),
    RED(Color.fromRGB(255, 85, 85), ChatColor.RED, DyeColor.RED),
    BLUE(Color.fromRGB(85, 85, 255), ChatColor.BLUE, DyeColor.LIGHT_BLUE),
    YELLOW(Color.fromRGB(255, 255, 85), ChatColor.YELLOW, DyeColor.YELLOW),
    AQUA(Color.fromRGB(85, 255, 255), ChatColor.AQUA, DyeColor.CYAN),
    BLACK(Color.BLACK, ChatColor.BLACK, DyeColor.BLACK),
    GOLD(Color.fromRGB(255, 170, 0), ChatColor.GOLD, DyeColor.ORANGE),
    DARK_BLUE(Color.fromRGB(0, 0, 170), ChatColor.DARK_BLUE, DyeColor.BLUE),
    DARK_GREEN(Color.fromRGB(0, 170, 0), ChatColor.DARK_GREEN, DyeColor.GREEN),
    DARK_RED(Color.fromRGB(170, 0, 0), ChatColor.DARK_RED, DyeColor.BROWN),
    DARK_PURPLE(Color.fromRGB(170, 0, 170), ChatColor.DARK_PURPLE, DyeColor.MAGENTA),
    GRAY(Color.fromRGB(170, 170, 170), ChatColor.GRAY, DyeColor.SILVER),
    DARK_GRAY(Color.fromRGB(85, 85, 85), ChatColor.DARK_GRAY, DyeColor.GRAY),
    LIGHT_PURPLE(Color.fromRGB(255, 85, 255), ChatColor.LIGHT_PURPLE, DyeColor.PINK),
    WHITE(Color.WHITE, ChatColor.WHITE, DyeColor.WHITE);

    private final Color color;          // Bukkit 的 Color 对象
    private final ChatColor chatColor; // 聊天颜色
    private final DyeColor dyeColor;   // 染料颜色

    /**
     * 构造函数，初始化颜色、聊天颜色和染料颜色。
     *
     * @param color      Bukkit 的 Color 对象
     * @param chatColor  聊天颜色
     * @param dyeColor   染料颜色
     */
    TeamColor(Color color, ChatColor chatColor, DyeColor dyeColor) {
        this.color = color;
        this.chatColor = chatColor;
        this.dyeColor = dyeColor;
    }

    /**
     * 获取 Bukkit 的 Color 对象。
     *
     * @return Bukkit 的 Color 对象
     */
    public Color getColor() {
        return color;
    }

    /**
     * 获取聊天颜色。
     *
     * @return 聊天颜色
     */
    public ChatColor getChatColor() {
        return chatColor;
    }

    /**
     * 获取染料颜色。
     *
     * @return 染料颜色
     */
    public DyeColor getDyeColor() {
        return dyeColor;
    }

    /**
     * 根据名称获取 TeamColor 枚举值。
     *
     * @param name 颜色名称
     * @return 对应的 TeamColor 枚举值，如果未找到则返回 null
     */
    public static TeamColor fromName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        try {
            return TeamColor.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
