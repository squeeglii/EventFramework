package me.squeeglii.plugin.eventfw.session.type;

import me.squeeglii.plugin.eventfw.session.EventInstance;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class DebugEvent extends EventInstance {

    private AtomicInteger tickCounter;

    @Override
    public void onStart() {
        this.tickCounter = new AtomicInteger(0);
    }

    @Override
    public void onPlayerAdd(Player player) {
        player.sendMessage("Welcome to DebugLandTM !!1!");
    }

    @Override
    public void onPlayerLeave(Player player) {
        player.sendMessage("Bye bye!");
    }

    @Override
    public void onTick() {
        for(Player player: this.getPlayerList()) {
            player.sendMessage("Tick number: %s".formatted(this.tickCounter.getAndIncrement()));
        }
    }

    @Override
    public void onStop() {

    }

}
