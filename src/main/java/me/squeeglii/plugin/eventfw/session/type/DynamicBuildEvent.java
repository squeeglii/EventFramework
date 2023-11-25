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
    public void onPrePlayerAdd(Player player) {
        PlayerSnapshot snapshot = new PlayerSnapshot(player);
        this.playerStates.put(player.getUniqueId(), snapshot);
    }

    @Override
    public void onPlayerAdd(Player player) {
        player.getInventory().clear();
        player.setGameMode(GameMode.CREATIVE);
        player.setTotalExperience(0);
        player.clearActivePotionEffects();

        TextUtil.send("Set your gamemode to Creative! Happy building!", player);
    }

    @Override
    public void onPlayerLeave(Player player) {
        EventFramework.plugin().getLogger().info("dyna build");
        PlayerSnapshot oldSnapshot = this.playerStates.remove(player.getUniqueId());
        oldSnapshot.reapplyTo(player);
        player.setFallDistance(0);

        EventFramework.plugin().getLogger().info("reapplied and reset");

        TextUtil.send("Restored your old inventory & placed you back where you left off.", player);
    }

    @Override
    public void onTick() {

    }

}
