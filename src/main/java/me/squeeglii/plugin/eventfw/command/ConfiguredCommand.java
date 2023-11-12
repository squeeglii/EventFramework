package me.squeeglii.plugin.eventfw.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

public abstract class ConfiguredCommand implements CommandExecutor {

    private final String id;

    public ConfiguredCommand(String id) {
        this.id = id.trim().toLowerCase();
    }

    public abstract LiteralCommandNode<?> configureTabCompletion();

    public String getId() {
        return this.id;
    }
}
