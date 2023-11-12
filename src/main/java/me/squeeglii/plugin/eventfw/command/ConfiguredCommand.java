package me.squeeglii.plugin.eventfw.command;

import dev.jorel.commandapi.CommandAPICommand;

public abstract class ConfiguredCommand {

    private final String id;

    public ConfiguredCommand(String id) {
        this.id = id.trim().toLowerCase();
    }

    public abstract CommandAPICommand buildCommand();

    public String getId() {
        return this.id;
    }
}
