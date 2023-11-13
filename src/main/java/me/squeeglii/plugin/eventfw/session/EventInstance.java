package me.squeeglii.plugin.eventfw.session;

import me.squeeglii.plugin.eventfw.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class EventInstance {

    // activity
    private boolean hasStarted;

    private Set<Player> playerList;


    // configuration
    private String name;
    private String description;
    private WorldBorder areaBounds;


    public EventInstance() {
        this.hasStarted = false;
        this.playerList = new HashSet<>();

        this.name = "General Shenanigans!";
        this.description = "...";

        this.areaBounds = null;
    }


    // Attempts to add a player to the event - only fails if they're already in it.
    public boolean addPlayer(Player player) {
        if(this.playerList.contains(player))
            return false;

        if(this.areaBounds != null) {
            player.setWorldBorder(this.areaBounds);
        }


        return true;
    }

    // Attempts to remove a player from the event - only fails if they're not in it.
    public boolean removePlayer(Player player) {
        if(!this.playerList.contains(player))
            return false;

        player.setWorldBorder(null);
        return true;
    }

    public void stop() {
        if(!this.hasStarted) return;

        this.hasStarted = false;
    }


    // What to show players when they join the server if this instance
    // is running.
    public Optional<Component> getJoinHint() {
        return Optional.of(Component.join(
                JoinConfiguration.separator(Component.text(" ")),
                Component.text("E | ").color(TextColor.color(TextUtil.BRANDING)),
                Component.text("We're currently running the '").color(NamedTextColor.GRAY),
                Component.text(this.name)
                         .style(Style.style(
                                 TextUtil.BRANDING,
                                 Set.of(TextDecoration.UNDERLINED, TextDecoration.ITALIC)
                         )),
                Component.text("' event! Join in with ").color(NamedTextColor.GRAY),
                Component.text("/join!")
                         .style(Style.style(
                                 TextUtil.BRANDING,
                                 Set.of(TextDecoration.UNDERLINED, TextDecoration.ITALIC)
                         ))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to Run!")))
                        .clickEvent(ClickEvent.runCommand("/join")),
                Component.text("\n"),
                Component.text("Description:\"%s\"".formatted(this.description)).color(NamedTextColor.GRAY)
        ));
    }


    public Set<Player> getPlayerList() {
        return Collections.unmodifiableSet(this.playerList);
    }

    public boolean hasStarted() {
        return this.hasStarted;
    }

}
