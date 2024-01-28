package me.squeeglii.plugin.eventfw.session.type.blockhunt;

import me.squeeglii.plugin.eventfw.SpigotUtil;
import me.squeeglii.plugin.eventfw.exception.InvalidConfigurationException;
import me.squeeglii.plugin.eventfw.session.EventInstance;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.joml.Math;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BlockHuntEvent extends EventInstance {

    private Team hiderTeam;
    private Team seekerTeam;
    private Team spectatorsTeam;

    private HashMap<Player, HiddenBlockTracker> blockTracker;

    private boolean isRoundRunning;

    private float seekerSplitFraction = 0.1f;
    private int minSeekers = 1;

    private Location seekerSpawnpoint = null;


    public BlockHuntEvent() {
        this.hiderTeam = null;
        this.seekerTeam = null;
        this.spectatorsTeam = null;

        this.blockTracker = new HashMap<>();

        this.isRoundRunning = false;
    }


    @Override
    public void onStart() throws InvalidConfigurationException {
        if(this.seekerSpawnpoint == null)
            throw new InvalidConfigurationException("'bh_seeker_spawn' must be set!");

        this.setUseTemporaryPlayers(true); // always override this.
        this.initTeams();
    }

    @Override
    public void onStop() {
        this.hiderTeam.unregister();
        this.seekerTeam.unregister();
        this.spectatorsTeam.unregister();
    }


    @Override
    public void onPlayerAdd(Player player) {
        if(this.isRoundRunning)
            this.spectatorsTeam.addPlayer(player);

        player.setGameMode(GameMode.SPECTATOR);
        player.getInventory().clear();
        player.setHealth(20.0f);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.clearActivePotionEffects();
    }

    @Override
    public void onPlayerLeave(Player player) {
        HiddenBlockTracker tracker = this.blockTracker.remove(player);

        if(tracker != null)
            tracker.disable();
    }

    @Override
    public void onTick() {
        if(this.isRoundRunning)
            return;

        this.blockTracker.values().forEach(HiddenBlockTracker::tick);
    }

    @EventHandler
    private void onDeath(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        // Check entity is a player that is a part of this event.
        if(!(entity instanceof Player player)) return;
        if(!this.getPlayerList().contains(player)) return;

        // 'Kill' if half a heart or less.
        if(player.getHealth() - event.getFinalDamage() > 1.0f)
            return;

        event.setCancelled(true);


        if(this.hiderTeam.hasPlayer(player)) {
            // switch to seekers.
            HiddenBlockTracker tracker = this.blockTracker.get(player);

            if(tracker != null)
                tracker.disable();

            this.hiderTeam.removePlayer(player);
            this.seekerTeam.addPlayer(player);
        }

        if(this.seekerTeam.hasPlayer(player)) {
            // respawn with timer?
            Location seekerSpawn = this.getValidSeekerSpawnpoint();
            player.teleport(seekerSpawn);

            PlayerInventory inventory = player.getInventory();

            inventory.clear();
            inventory.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
            inventory.setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            inventory.setBoots(new ItemStack(Material.IRON_BOOTS));

            ItemStack swordItem = new ItemStack(Material.IRON_SWORD);
            ItemMeta meta = swordItem.getItemMeta();
            meta.setUnbreakable(true);

            swordItem.setItemMeta(meta);

            inventory.setItem(0, swordItem);
            inventory.setHeldItemSlot(0);

            return;
        }

        if(this.spectatorsTeam.hasPlayer(player)) {
            // respawn instantly
            return;
        }
    }


    private void startRound() {
        if(this.isRoundRunning)
            return;

        this.blockTracker.clear();

        for(Player player: this.getPlayerList()) {
            HiddenBlockTracker tracker = new HiddenBlockTracker(player, this, Material.BRICKS.createBlockData());
        }



        this.isRoundRunning = true;
    }




    protected void initTeams() {
        Scoreboard scoreboard = SpigotUtil.getServerScoreboard();
        this.hiderTeam = scoreboard.registerNewTeam("e-hide-"+UUID.randomUUID());
        this.hiderTeam.color(NamedTextColor.GREEN);
        this.hiderTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        this.hiderTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        this.hiderTeam.setAllowFriendlyFire(false);

        this.seekerTeam = scoreboard.registerNewTeam("e-seek-"+UUID.randomUUID());
        this.seekerTeam.color(NamedTextColor.RED);
        this.seekerTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        this.seekerTeam.setAllowFriendlyFire(false);

        this.spectatorsTeam = scoreboard.registerNewTeam("e-spec-"+UUID.randomUUID());
        this.spectatorsTeam.color(NamedTextColor.DARK_GRAY);
        this.spectatorsTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        this.spectatorsTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        this.spectatorsTeam.setCanSeeFriendlyInvisibles(true);
    }

    protected BlockHuntTeam balanceIntoTeam(Player player) {
        int playerCount = this.getPlayerList().size();
        int playerCountFracAsSeekers = (int) Math.ceil(playerCount / this.seekerSplitFraction); // greedy
        int seekerTarget = Math.max(playerCountFracAsSeekers, this.minSeekers);

        this.hiderTeam.removePlayer(player);
        this.seekerTeam.removePlayer(player);
        this.spectatorsTeam.removePlayer(player);

        // Seeker team is greedy with its fraction - i.e 0.99 will grab 100% of the players unless there's 100 players
        // soooo, prioritise the hiders if it's empty.
        if(this.hiderTeam.getSize() <= 0) {
            this.hiderTeam.addPlayer(player);
            return BlockHuntTeam.HIDER;
        }

        if(this.seekerTeam.getSize() < seekerTarget) {
            this.seekerTeam.addPlayer(player);
            return BlockHuntTeam.SEEKER;
        } else {
            this.hiderTeam.addPlayer(player);
            return BlockHuntTeam.HIDER;
        }
    }

    private Set<BlockData> getRemainingBlocks() {
        return this.blockTracker.values().stream()
                .filter(HiddenBlockTracker::isEnabled)
                .map(HiddenBlockTracker::getBlock)
                .collect(Collectors.toSet());
    }

    public void setSeekerSplitFraction(float seekerSplitFraction) {
        this.seekerSplitFraction = seekerSplitFraction;
    }

    public void setMinSeekers(int minSeekers) {
        this.minSeekers = minSeekers;
    }

    public void setSeekerSpawnpoint(Location seekerSpawnpoint) {
        this.seekerSpawnpoint = seekerSpawnpoint;
    }

    private Location getValidSeekerSpawnpoint() {
        if(this.seekerSpawnpoint == null) {

        }
    }
}
