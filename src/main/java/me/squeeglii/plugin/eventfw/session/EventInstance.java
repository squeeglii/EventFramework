package me.squeeglii.plugin.eventfw.session;

import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

public class EventInstance {

    private boolean hasStarted;

    private WorldBorder areaBounds;

    public EventInstance() {
        this.hasStarted = false;
    }


    public boolean offerPlayer(Player player) {


        if(this.areaBounds != null) {
            player.setWorldBorder(this.areaBounds);
        }


        return true;
    }

    public boolean removePlayer(Player player) {
        player.setWorldBorder(null);
        return true;
    }

    public void stop() {
        if(!this.hasStarted) return;

        this.hasStarted = false;
    }


    public boolean hasStarted() {
        return this.hasStarted;
    }

}
