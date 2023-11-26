package me.squeeglii.plugin.eventfw.session.type;

import me.squeeglii.plugin.eventfw.EventFramework;
import me.squeeglii.plugin.eventfw.TextUtil;
import me.squeeglii.plugin.eventfw.archive.PlayerSnapshot;
import me.squeeglii.plugin.eventfw.session.EventInstance;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.UUID;

public class DynamicBuildEvent extends EventInstance implements Listener {

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
