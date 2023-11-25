package me.squeeglii.plugin.eventfw.session;

import me.squeeglii.plugin.eventfw.EventFramework;
import me.squeeglii.plugin.eventfw.Permission;
import me.squeeglii.plugin.eventfw.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public abstract class EventInstance implements EventAPI {

    // activity
    private boolean hasStarted;

    private final Set<Player> playerList;
    private final Set<Player> leavingPlayers;
    private World world;
    private BukkitTask tickTask;


    // configuration
    private String name;
    private String description;
    private int playerLimit; // Advisory -- /join will stop working but plugins can still force add players.
    private WorldBorder areaBounds;
    private Location spawnpoint;
    private boolean shouldAnnounceEvent;
    private boolean preventDimensionSwitches;

    private NamespacedKey worldId;


    public EventInstance() {
        this.hasStarted = false;
        this.playerList = new HashSet<>();
        this.leavingPlayers = new HashSet<>();

        this.name = "General Shenanigans!";
        this.description = "...";

        this.playerLimit = 1000;

        this.areaBounds = null;

        this.shouldAnnounceEvent = true;

        this.spawnpoint = null;

        this.world = null;
        this.worldId = null;
    }


    public final void start() {
        if(this.hasStarted)
            return;

        this.world = this.worldId == null
                ? EventFramework.plugin().getServer().getWorlds().get(0)
                : EventFramework.plugin().getServer().getWorld(this.worldId);



        this.onStart();

        this.tickTask = EventFramework.plugin()
                .getServer()
                .getScheduler()
                .runTaskTimer(EventFramework.plugin(), this::onTick, 1, 1);

        this.hasStarted = true;
    }

    public final void stop() {
        if(!this.hasStarted) return;
        this.hasStarted = false;

        this.tickTask.cancel();
        this.onStop();

        for(Player player: new HashSet<>(this.playerList))
            this.removePlayer(player);
    }

    // Safer method to add players - does more checks & gives a more detailed response
    public final EventJoinResult offerPlayer(Player player) {
        if(!this.hasStarted)
            return EventJoinResult.EVENT_HAS_NOT_STARTED;

        if(this.playerList.contains(player))
            return EventJoinResult.PLAYER_ALREADY_IN_EVENT;

        if(this.playerList.size() >= this.playerLimit && !player.hasPermission(Permission.BYPASS_PLAYER_LIMIT))
            return EventJoinResult.EVENT_FULL;

        if(!this.onPlayerJoinTest(player))
            return EventJoinResult.EVENT_IMPL_DENIED;

        try {
            return this.addPlayer(player)
                    ? EventJoinResult.SUCCESS
                    : EventJoinResult.UNHANDLED_FAILURE;

        } catch (Exception err) {
            err.printStackTrace();
            return EventJoinResult.ERROR;
        }
    }

    // Attempts to add a player to the event - only fails if they're already in it.
    public final boolean addPlayer(Player player) {
        if(!this.hasStarted)
            return false;

        if(this.playerList.contains(player))
            return false;

        this.onPrePlayerAdd(player);

        Location spawn = this.getSpawn();

        if(spawn != null) {
            player.teleport(spawn, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }

        if(this.areaBounds != null) {

            // Fallback spawnpoint.
            if(this.spawnpoint == null) {
                Location center = this.areaBounds.getCenter();
                int blockX = center.getBlockX();
                int blockZ = center.getBlockZ();

                double spawnY = this.world.getHighestBlockYAt(blockX, blockZ, HeightMap.MOTION_BLOCKING) + 1.1;
                double spawnX = center.getBlockX() + 0.5;
                double spawnZ = blockZ + 0.5;
                Location newHighestCenter = new Location(this.world, spawnX, spawnY, spawnZ);

                player.teleport(newHighestCenter, PlayerTeleportEvent.TeleportCause.PLUGIN);
            }

            player.setWorldBorder(this.areaBounds);
        }

        this.playerList.add(player);
        this.onPlayerAdd(player);
        return true;
    }

    // Attempts to remove a player from the event - only fails if they're not in it.
    public final boolean removePlayer(Player player) {
        if(!this.playerList.contains(player))
            return false;

        this.leavingPlayers.add(player);

        player.setWorldBorder(null);

        this.onPlayerLeave(player);
        this.playerList.remove(player);
        this.leavingPlayers.remove(player);
        return true;
    }


    public boolean isPlayerLeaving(Player player) {
        return this.leavingPlayers.contains(player);
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPlayerLimit(int playerLimit) {
        this.playerLimit = playerLimit;
    }

    public void setAreaBounds(WorldBorder areaBounds) {
        this.areaBounds = areaBounds;

        // Update world border for all participants if the event has already started.
        if(!this.hasStarted()) return;

        for(Player player: this.playerList) {
            player.setWorldBorder(this.areaBounds);
        }
    }

    public void setSpawn(Location spawnpoint) {
        this.spawnpoint = spawnpoint;
    }

    public void setShouldAnnounceEvent(boolean shouldAnnounceEvent) {
        this.shouldAnnounceEvent = shouldAnnounceEvent;
    }

    public void setHostingWorldId(NamespacedKey worldId) {
        this.worldId = worldId;
    }

    public void setShouldPreventDimensionSwitches(boolean preventDimensionSwitches) {
        this.preventDimensionSwitches = preventDimensionSwitches;
    }

    // What to show players when they join the server if this instance
    // is running.
    public Optional<Component> getJoinHint() {
        if(!this.shouldAnnounceEvent)
            return Optional.empty();

        return Optional.of(Component.join(
                JoinConfiguration.separator(Component.text(" ")),
                Component.text("E | ").color(TextColor.color(TextUtil.BRANDING)),
                Component.text("We're currently running the '").color(NamedTextColor.GRAY),
                Component.text(this.name)
                         .style(Style.style(
                                 TextUtil.BRANDING,
                                 Set.of(TextDecoration.UNDERLINED, TextDecoration.ITALIC)
                         )),
                Component.text("' event! Join in with ").color(NamedTextColor.GRAY),
                Component.text("/join!")
                         .style(Style.style(
                                 TextUtil.BRANDING,
                                 Set.of(TextDecoration.UNDERLINED, TextDecoration.ITALIC)
                         ))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to Run!")))
                        .clickEvent(ClickEvent.runCommand("/join")),
                Component.text("\n"),
                Component.text("Description:\"%s\"".formatted(this.description)).color(NamedTextColor.GRAY)
        ));
    }


    public Set<Player> getPlayerList() {
        return Collections.unmodifiableSet(this.playerList);
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public WorldBorder getBorder() {
        return this.areaBounds;
    }

    public Location getSpawn() {
        return this.spawnpoint == null
                ? null
                : new Location(
                        this.world,
                        this.spawnpoint.x(), this.spawnpoint.y(), this.spawnpoint.z(),
                        this.spawnpoint.getYaw(), this.spawnpoint.getPitch()
                );
    }

    public int getPlayerLimit() {
        return this.playerLimit;
    }

    public boolean shouldAnnounceEvent() {
        return this.shouldAnnounceEvent;
    }

    public boolean shouldPreventDimensionSwitches() {
        return this.preventDimensionSwitches;
    }

    public World getWorld() {
        return this.world;
    }

    public boolean hasStarted() {
        return this.hasStarted;
    }

}
