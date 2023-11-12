package me.squeeglii.plugin.eventfw.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class JoinCommand extends ConfiguredCommand {


    public JoinCommand() {
        super("join");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public LiteralCommandNode<?> configureTabCompletion() {
        return LiteralArgumentBuilder.literal(this.getId()).then(
                RequiredArgumentBuilder.argument("id", StringArgumentType.word())
        ).build();
    }
}
