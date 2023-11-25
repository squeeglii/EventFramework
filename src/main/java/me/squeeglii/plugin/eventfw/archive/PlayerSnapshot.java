package me.squeeglii.plugin.eventfw.archive;

import net.minecraft.world.level.block.Blocks;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class PlayerSnapshot {

    private UUID copiedPlayerId;

    private GameMode gameMode;

    private final double health;
    private final int food;
    private final int exp;

    private final Location lastPosition;

    private List<PotionEffect> potionEffects;

    private ItemStack[] items;


    public PlayerSnapshot(Player player) {
        this.copiedPlayerId = player.getUniqueId();

        this.gameMode = player.getGameMode();

        this.health = player.getHealth();
        this.food = player.getFoodLevel();
        this.exp = player.getTotalExperience();

        this.lastPosition = new Location(
                player.getWorld(),
                player.getX(), player.getY(), player.getZ(),
                player.getYaw(), player.getPitch()
        );

        this.savePotionEffects(player);
        this.saveInventory(player);
    }

    public void reapplyTo(Player player) {
        player.setGameMode(this.gameMode);

        player.setHealth(this.health);
        player.setFoodLevel(this.food);
        player.setTotalExperience(this.exp);

        // todo: save advancements + recipes.
        // there doesn't seem to be an API call for getting all at once?

        this.restorePotionEffectsFor(player);
        this.restoreInventoryFor(player);

        player.teleport(this.lastPosition, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }


    private void savePotionEffects(Player player) {
        Collection<PotionEffect> potionEffects = player.getActivePotionEffects();
        List<PotionEffect> storedEffects = new LinkedList<>();

        potionEffects.forEach(effect -> {
            PotionEffect clonedEffect = new PotionEffect(
                    effect.getType(),
                    effect.getDuration(),
                    effect.getAmplifier(),
                    effect.isAmbient(),
                    effect.hasParticles(),
                    effect.hasIcon()
            );
            storedEffects.add(clonedEffect);
        });

        this.potionEffects = storedEffects;
    }

    private void restorePotionEffectsFor(Player player) {
        player.clearActivePotionEffects();
        player.addPotionEffects(this.potionEffects);
    }

    private void saveInventory(Player player) {
        PlayerInventory inventory = player.getInventory();

        ItemStack[] oldContents = inventory.getContents();
        ItemStack[] newContents = Arrays.stream(oldContents)
                .map(stack -> stack == null
                        ? new ItemStack(Material.AIR, 1)
                        : stack
                )
                .map(ItemStack::new)
                .toArray(ItemStack[]::new);

        this.items = newContents;
    }

    private void restoreInventoryFor(Player player) {
        ItemStack[] copiedContents = Arrays.stream(this.items)
                .map(stack -> stack == null
                        ? new ItemStack(Material.AIR, 1)
                        : stack
                )
                .map(ItemStack::new)
                .toArray(ItemStack[]::new);

       player.getInventory().setContents(copiedContents);
    }

}
