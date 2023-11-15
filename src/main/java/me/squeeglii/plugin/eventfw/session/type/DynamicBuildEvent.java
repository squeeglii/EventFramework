package me.squeeglii.plugin.eventfw.session.type;

import me.squeeglii.plugin.eventfw.EventFramework;
import me.squeeglii.plugin.eventfw.TextUtil;
import me.squeeglii.plugin.eventfw.archive.PlayerSnapshot;
import me.squeeglii.plugin.eventfw.session.EventInstance;
import org.bukkit.GameMode;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.UUID;

public class DynamicBuildEvent extends EventInstance {

    // Used to restore pre-creative state.
    private HashMap<UUID, PlayerSnapshot> playerStates;

    public DynamicBuildEvent() {
        super();
    }


    @Override
    public void onStart() {
        this.playerStates = new HashMap<>();
    }

    @Override
    public void onStop() {

    }

    @Override
    public void onPlayerAdd(Player player) {
        PlayerSnapshot snapshot = new PlayerSnapshot(player);
        this.playerStates.put(player.getUniqueId(), snapshot);

        player.getInventory().clear();
        player.setGameMode(GameMode.CREATIVE);
        player.setTotalExperience(0);
        player.clearActivePotionEffects();

        TextUtil.send("Set your gamemode to Creative! Happy building!", player);
    }

    @Override
    public void onPlayerLeave(Player player) {
        PlayerSnapshot oldSnapshot = this.playerStates.remove(player.getUniqueId());
        oldSnapshot.reapplyTo(player);

        Location playerPos = player.getLocation();
        double safeY = this.getWorld().getHighestBlockYAt(
                playerPos.getBlockX(),
                playerPos.getBlockZ(),
                HeightMap.MOTION_BLOCKING
        ) + 1.1f;

        Location safeLocation = new Location(
                playerPos.getWorld(),
                playerPos.getBlockX(), safeY, playerPos.getBlockZ(),
                playerPos.getYaw(), playerPos.getPitch()
        );

        player.setFallDistance(0);
        player.teleport(safeLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);

        TextUtil.send("Restored your old inventory & placed you somewhere safe.", player);
    }

    @Override
    public void onTick() {

    }

}
