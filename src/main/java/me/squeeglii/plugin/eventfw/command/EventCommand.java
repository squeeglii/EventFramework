package me.squeeglii.plugin.eventfw.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.StringArgument;

import java.util.List;

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
                this.getConfigCommand(),
                this.getLaunchCommand(),
                this.getKickCommand()
        ));

        return cmd;
    }

    protected CommandAPICommand getCreateCommand() {
        return new CommandAPICommand("create")
                .withArguments(
                        new StringArgument("type"),
                        new StringArgument("id")
                ).executes((sender, args) -> {
                    sender.sendMessage("create w/ type: %s, id: %s".formatted(
                            args.get("type"), args.get("id")
                    ));
                });
    }

    protected CommandAPICommand getConfigCommand() {
        CommandAPICommand cmd =  new CommandAPICommand("configure")
                .executes((sender, args) -> {
                    sender.sendMessage("config w/ ...");
                });
        return cmd;
    }

    protected CommandAPICommand getLaunchCommand() {
        return new CommandAPICommand("launch")
                .withArguments(
                        new StringArgument("id").setOptional(true)
                ).executes((sender, args) -> {
                    sender.sendMessage("launch w/ id: %s".formatted(args.get("id")));
                });
    }

    protected CommandAPICommand getKickCommand() {
        return new CommandAPICommand("kick")
                .withArguments(
                        new EntitySelectorArgument.ManyPlayers("player")
                ).executes((sender, args) -> {

                });
    }
}
