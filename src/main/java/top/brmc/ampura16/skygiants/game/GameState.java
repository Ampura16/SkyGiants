package top.brmc.ampura16.skygiants.game;

public enum GameState {
    WAITING("等待中"),    // 等待中
    RUNNING("进行中"),    // 游戏中
    STOPPED("已停止");    // 已停止

    private final String displayName;

    GameState(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
