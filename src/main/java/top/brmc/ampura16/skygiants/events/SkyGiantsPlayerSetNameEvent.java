package top.brmc.ampura16.skygiants.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import top.brmc.ampura16.skygiants.game.Team;

public class SkyGiantsPlayerSetNameEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private String displayName;
    private String playerListName;
    private final Player player;
    private final Team team;

    public SkyGiantsPlayerSetNameEvent(Team team, String displayName, String playerListName, Player player) {
        this.team = team;
        this.displayName = displayName;
        this.playerListName = playerListName;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPlayerListName() {
        return playerListName;
    }

    public void setPlayerListName(String playerListName) {
        this.playerListName = playerListName;
    }

    public Player getPlayer() {
        return player;
    }

    public Team getTeam() {
        return team;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
