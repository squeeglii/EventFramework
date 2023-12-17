package me.squeeglii.plugin.eventfw.session.type.blockhunt;

import me.squeeglii.plugin.eventfw.SpigotUtil;
import me.squeeglii.plugin.eventfw.session.EventInstance;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

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


    public BlockHuntEvent() {
        this.hiderTeam = null;
        this.seekerTeam = null;
        this.spectatorsTeam = null;

        this.blockTracker = new HashMap<>();

        this.isRoundRunning = false;
    }


    @Override
    public void onStart() {
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

    }

    @Override
    public void onPlayerLeave(Player player) {

    }

    @Override
    public void onTick() {

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

    private Set<Block> getRemainingBlocks() {
        return this.blockTracker.values().stream()
                .filter(HiddenBlockTracker::isApplied)
                .map(HiddenBlockTracker::getBlock)
                .collect(Collectors.toSet());
    }

}
