package me.squeeglii.plugin.eventfw.session.type;

import me.squeeglii.plugin.eventfw.TextUtil;
import me.squeeglii.plugin.eventfw.session.EventInstance;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class DynamicBuildEvent extends EventInstance implements Listener {

    public DynamicBuildEvent() {
        super();
        this.setUseTemporaryPlayers(true);
        this.setDisableEnderChests(true);
        this.setDisableEnderChests(true);
        this.setShouldPreventDimensionSwitching(true);

        // a border is recommended on top of all of this but this is hard to automatically do.
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

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
        player.setFallDistance(0);
        TextUtil.send("Restored your old inventory & placed you back where you left off.", player);
    }

    @Override
    public void onTick() {

    }



}
