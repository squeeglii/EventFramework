package me.squeeglii.plugin.eventfw.command;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public abstract class ConfiguredCommand implements CommandExecutor, TabCompleter {

    private final String id;

    public ConfiguredCommand(String id) {
        this.id = id.trim().toLowerCase();
    }


    public String getId() {
        return this.id;
    }
}
