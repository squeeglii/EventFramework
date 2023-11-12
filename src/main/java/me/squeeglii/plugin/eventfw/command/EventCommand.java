package me.squeeglii.plugin.eventfw.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
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
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String joinedArgs = String.join(" ", args);
        String joinedCommand = String.join(" ", label, joinedArgs);

        boolean isValid = this.configureTabCompletion().isValidInput(joinedCommand);

        sender.sendMessage("IsValid? %s".formatted(isValid));

        return false;
    }

    @Override
    public LiteralCommandNode<?> configureTabCompletion() {

        LiteralArgumentBuilder<Object> root = LiteralArgumentBuilder.literal(this.getId());

        LiteralArgumentBuilder<Object> create = LiteralArgumentBuilder.literal("create");
        LiteralArgumentBuilder<Object> launch = LiteralArgumentBuilder.literal("launch");
        LiteralArgumentBuilder<Object> config = LiteralArgumentBuilder.literal("config");
        LiteralArgumentBuilder<Object> kick = LiteralArgumentBuilder.literal("kick");


        create.then(RequiredArgumentBuilder.argument("event_type", StringArgumentType.word())
                .then(RequiredArgumentBuilder.argument("id", StringArgumentType.word())));

        launch.then(RequiredArgumentBuilder.argument("id", StringArgumentType.word()));
        //kick.then(RequiredArgumentBuilder.argument("id", StringArgumentType.word()));

        return root
                .then(create)
                //.then(config)
                .then(launch)
                //.then(kick)
                .build();
    }
}
