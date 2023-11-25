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

    // Used to restore pre-creative state.
    private HashMap<UUID, PlayerSnapshot> playerStates;

    public DynamicBuildEvent() {
        super();
    }


    @Override
    public void onStart() {
        this.playerStates = new HashMap<>();

        EventFramework.plugin().registerListener(this);
    }

    @Override
    public void onStop() {
        EventFramework.plugin().unregisterListener(this);
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


    @EventHandler(priority = EventPriority.LOW)
    public void onOpenEndChest(PlayerInteractEvent event) {
        Block wasClicked = event.getClickedBlock();

        if(!this.getPlayerList().contains(event.getPlayer()))
            return;

        if(wasClicked == null)
            return;

        if(wasClicked.getType() != Material.ENDER_CHEST)
            return;

        if(event.getAction() == Action.RIGHT_CLICK_BLOCK)

        event.setCancelled(true);
        Component message = Component.text(
                "Nuh uh! You can't use an ender-chest while in a build event!",
                NamedTextColor.RED
        );

        event.getPlayer().sendMessage(message);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDropItem(PlayerDropItemEvent event) {
        if(!this.getPlayerList().contains(event.getPlayer()))
            return;

        event.setCancelled(true);
        Component message = Component.text(
                "Nuh uh! You're not allowed to drop items while in a build event!",
                NamedTextColor.RED
        );

        event.getPlayer().sendMessage(message);
    }


}
