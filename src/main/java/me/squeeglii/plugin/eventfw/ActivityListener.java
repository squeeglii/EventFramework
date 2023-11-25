package me.squeeglii.plugin.eventfw;

import me.squeeglii.plugin.eventfw.config.Cfg;
import me.squeeglii.plugin.eventfw.session.EventInstance;
import me.squeeglii.plugin.eventfw.session.EventManager;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Optional;

public class ActivityListener implements Listener {


    @EventHandler
    private void showJoinMessage(PlayerJoinEvent event) {
        boolean showEventHint = Cfg.ENABLE_JOIN_EVENT_HINT.main().orElse(true);
        boolean overrideJoinMessage = Cfg.OVERRIDE_JOIN_MESSAGE.main().orElse(false);

        if(overrideJoinMessage)
            event.joinMessage(Component.join(JoinConfiguration.noSeparators(),
                    Component.text("+ | "),
                    event.getPlayer().displayName().color(NamedTextColor.DARK_GRAY)
            ));

        if(EventManager.main().isEventRunning() && showEventHint) {
            EventInstance eventInstance = EventManager.main().getCurrentEvent();
            Optional<Component> optHint = eventInstance.getJoinHint();

            optHint.ifPresent(hint -> Run.syncDelayed(60, () -> {
                Player p = event.getPlayer();
                p.sendMessage(hint);
                p.playSound(Sound.sound()
                                 .type(org.bukkit.Sound.BLOCK_NOTE_BLOCK_BIT)
                                 .pitch(1.2f)
                                 .volume(0.2f)
                                 .source(Sound.Source.VOICE)
                                 .build()
                );
            }));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerLeave(PlayerQuitEvent event) {
        Player p = event.getPlayer();

        EventFramework.plugin().getLogger().info("LEAVE EVENT");

        if(EventManager.main().isPlayerParticipating(p)) {
            EventManager.main()
                        .getCurrentEvent()
                        .removePlayer(p);
        }
    }

    @EventHandler
    private void onTeleport(EntityTeleportEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if(!(event.getEntity() instanceof Player player)) return;
        if(!EventManager.main().isPlayerParticipating(player)) return;

        EventInstance e = EventManager.main().getCurrentEvent();

        // Only enforce this if the event has started.
        if(!e.hasStarted())
            return;

        // Only enforce this if prevention is enabled.
        if(!e.shouldPreventDimensionSwitches())
            return;

        // Ignore leaving players
        if(e.isPlayerLeaving(player))
            return;

        if(to == null) {
            player.sendMessage(Component.text("Blocked your teleport! Please leave the event first.", NamedTextColor.RED));
            event.setCancelled(true);
            return;
        }

        if(to.getWorld() != from.getWorld()) {
            player.sendMessage(Component.text("Blocked your teleport! You cannot switch dimensions while in an event.", NamedTextColor.RED));
            event.setCancelled(true);
        }
    }

}
