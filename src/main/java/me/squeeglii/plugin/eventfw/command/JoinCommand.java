package me.squeeglii.plugin.eventfw.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class JoinCommand extends ConfiguredCommand {


    public JoinCommand() {
        super("join");
    }


    @Override
    public CommandAPICommand buildCommand() {
        return new CommandAPICommand(this.getId())
                .withArguments(
                        new StringArgument("id").setOptional(true)
                ).executes((sender, args) -> {
                    sender.sendMessage("join w/ id: %s".formatted(args.get("id")));
                });
    }
}
