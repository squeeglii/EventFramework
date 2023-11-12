package me.squeeglii.plugin.eventfw.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class EventCommand extends ConfiguredCommand {

    private static final String[] SUB_COMMANDS = new String[] { "create", "configure", "launch", "kick" };

    // /event create <EventType: type> <string: id>
    // /event configure <name|description|max_players

    // /event launch [id]     -- if no id provided, launch the most recent event created.

    // /event kick

    public EventCommand() {
        super("event");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        return null;
    }
}
