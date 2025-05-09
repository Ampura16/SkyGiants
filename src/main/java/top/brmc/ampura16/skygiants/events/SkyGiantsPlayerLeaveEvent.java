package top.brmc.ampura16.skygiants.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import top.brmc.ampura16.skygiants.game.Game;

public class SkyGiantsPlayerLeaveEvent extends Event {
    private final Game game;
    private final Player player;

    public SkyGiantsPlayerLeaveEvent(Game game, Player player) {
        this.game = game;
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }
}
