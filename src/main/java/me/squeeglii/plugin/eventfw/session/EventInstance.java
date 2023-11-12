package me.squeeglii.plugin.eventfw.session;

import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

public class EventInstance {


    private WorldBorder areaBounds;


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

}
