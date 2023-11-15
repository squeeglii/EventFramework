package me.squeeglii.plugin.eventfw.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import me.squeeglii.plugin.eventfw.TextUtil;
import me.squeeglii.plugin.eventfw.session.EventInstance;
import me.squeeglii.plugin.eventfw.session.EventJoinResult;
import me.squeeglii.plugin.eventfw.session.EventManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class JoinCommand extends ConfiguredCommand {


    public JoinCommand() {
        super("join");
    }


    @Override
    public CommandAPICommand buildCommand() {
        return new CommandAPICommand(this.getId())
                .withRequirement(sender -> sender instanceof Player)
                .withArguments(
                        new StringArgument("id").setOptional(true)
                ).executesPlayer((sender, args) -> {

                    if(!EventManager.main().isEventRunning()) {
                        sender.sendMessage(Component.text("There are no events running!", NamedTextColor.RED));
                        return;
                    }

                    EventInstance event = EventManager.main().getCurrentEvent();
                    EventJoinResult result = event.offerPlayer(sender);

                    Component message = switch (result) {
                        case SUCCESS -> TextUtil.message("Joined event!");
                        case EVENT_FULL -> Component.text("The event is full! Try joining later.", NamedTextColor.RED);
                        case PLAYER_ALREADY_IN_EVENT -> Component.text("You're already in this event!", NamedTextColor.RED);
                        case EVENT_IMPL_DENIED -> Component.text("Entry denied.", NamedTextColor.RED);
                        case EVENT_HAS_NOT_STARTED -> Component.text("The main event hasn't started yet! Wait for an announcement.", NamedTextColor.RED);
                        case UNHANDLED_FAILURE -> Component.text("Something broke! Whoops! :) - Tell an admin.", NamedTextColor.RED);
                        case ERROR -> Component.text("Something unexpected occurred while trying to join!", NamedTextColor.RED);
                    };

                    sender.sendMessage(message);
                });
    }
}
