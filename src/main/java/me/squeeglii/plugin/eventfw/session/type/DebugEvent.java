package me.squeeglii.plugin.eventfw.session.type;

import me.squeeglii.plugin.eventfw.session.EventInstance;
import net.minecraft.world.item.Items;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
        int tickNum = this.tickCounter.getAndIncrement();

        boolean shouldGiveCooke = Math.floorMod(tickNum, 69) == 0;

        for(Player player: this.getPlayerList()) {
            player.sendMessage("Tick number: %s".formatted(tickNum));

            if(shouldGiveCooke) {
                player.getInventory().addItem(new ItemStack(Material.COOKIE, 1));
            }
        }
    }

    @Override
    public void onStop() {

    }

}
