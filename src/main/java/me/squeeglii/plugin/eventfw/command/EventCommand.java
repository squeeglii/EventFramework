package me.squeeglii.plugin.eventfw.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.squeeglii.plugin.eventfw.TextUtil;
import me.squeeglii.plugin.eventfw.session.EventInstance;
import me.squeeglii.plugin.eventfw.session.EventManager;
import me.squeeglii.plugin.eventfw.session.EventType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public class EventCommand extends ConfiguredCommand {

    private static final String[] SUB_COMMANDS = new String[] { "create", "configure", "launch", "kick" };

    // /event create <EventType: type> <string: id>
    // /event configure <name|description|max_players>

    // /event launch [id]     -- if no id provided, launch the most recent event created.

    // /event kick <player selector>

    public EventCommand() {
        super("event");
    }

    @Override
    public CommandAPICommand buildCommand() {
        CommandAPICommand cmd = new CommandAPICommand(this.getId());

        cmd.setSubcommands(List.of(
                this.getCreateCommand(),
                this.getStopCommand(),
                this.getConfigCommand(),
                this.getLaunchCommand(),
                this.getKickCommand()
        ));

        return cmd;
    }

    protected CommandAPICommand getCreateCommand() {
        return new CommandAPICommand("new")
                .withArguments(
                        new StringArgument("type")
                                .replaceSuggestions(ArgumentSuggestions.strings(EventType.allTypeIds()))
                ).executes((sender, args) -> {

                    String typeId = (String) args.get("type");
                    Optional<EventInstance> optInstance = EventType.getAndCreate(typeId);

                    if(EventManager.main().isEventRunning()) {
                        Component component = Component.text(
                                "There's already an event running! Run '/event stop' before starting a new one.",
                                NamedTextColor.RED
                        );

                        sender.sendMessage(component);
                        return;
                    }

                    if(optInstance.isEmpty()) {
                        Component component = Component.text("The type '%s' is unrecognised.".formatted(
                                typeId
                        ), NamedTextColor.RED);

                        sender.sendMessage(component);
                        return;
                    }

                    EventManager.main().setEvent(optInstance.get());
                    Component component = TextUtil.message("Created new event! Configure it with '/event configure' " +
                                                           "and start it with '/event launch'!");

                    sender.sendMessage(component);
                });
    }

    protected CommandAPICommand getStopCommand() {
        return new CommandAPICommand("stop")
                .withRequirement(sender -> EventManager.main().isEventRunning())
                .executes((sender, args) -> {
                    EventManager.main().stopCurrentEvent();

                    Component component = TextUtil.message("Stopped any running events.");
                    sender.sendMessage(component);
                });
    }

    protected CommandAPICommand getConfigCommand() {
        CommandAPICommand cmd =  new CommandAPICommand("configure")
                .executes((sender, args) -> {

                });
        return cmd;
    }

    protected CommandAPICommand getLaunchCommand() {
        return new CommandAPICommand("launch")
                .executes((sender, args) -> {

                });
    }

    protected CommandAPICommand getKickCommand() {
        return new CommandAPICommand("kick")
                .withRequirement(sender ->
                        sender instanceof Player player && EventManager.main().isPlayerParticipating(player))
                .withArguments(new EntitySelectorArgument.ManyPlayers("player"))
                .executes((sender, args) -> {

                });
    }
}
