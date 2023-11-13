package me.squeeglii.plugin.eventfw.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.wrappers.Location2D;
import me.squeeglii.plugin.eventfw.TextUtil;
import me.squeeglii.plugin.eventfw.config.data.Key;
import me.squeeglii.plugin.eventfw.session.EventInstance;
import me.squeeglii.plugin.eventfw.session.EventManager;
import me.squeeglii.plugin.eventfw.session.EventType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class EventCommand extends ConfiguredCommand {

    // /event new <EventType: type> <string: id>
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
        return new CommandAPICommand("configure")
                .withSubcommand(this.stringSetter("name", EventManager.main().getCurrentEvent()::setName))
                .withSubcommand(this.stringSetter("description", EventManager.main().getCurrentEvent()::setDescription))
                .withSubcommand(this.intSetter("player_limit", EventManager.main().getCurrentEvent()::setPlayerLimit))
                .withSubcommand(this.borderSetter("border", EventManager.main().getCurrentEvent()::setAreaBounds));
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


    private CommandAPICommand intSetter(String name, Consumer<Integer> setter) {
        return new CommandAPICommand(name)
                .withArguments(new IntegerArgument("value").setOptional(false))
                .executes((sender, args) -> {
                    if(EventManager.main().getCurrentEvent() == null) {
                        this.errorBecauseNoEvent(sender);
                        return;
                    }

                    Integer value = (Integer) args.get("value");

                    if(value == null) {
                        this.errorBecause(sender, "Invalid value!");
                        return;
                    }

                    setter.accept(value);
                    sender.sendMessage(TextUtil.message("Updated '%s' to '%s'!".formatted(name, value)));
                });
    }

    private CommandAPICommand stringSetter(String name, Consumer<String> setter) {
        return new CommandAPICommand(name)
                .withArguments(new GreedyStringArgument("value").setOptional(false))
                .executes((sender, args) -> {
                    if(EventManager.main().getCurrentEvent() == null) {
                        this.errorBecauseNoEvent(sender);
                        return;
                    }

                    String value = (String) args.get("value");

                    if(value == null) {
                        this.errorBecause(sender, "Invalid value!");
                        return;
                    }

                    setter.accept(value);
                    sender.sendMessage(TextUtil.message("Updated '%s' to '%s'!".formatted(name, value)));
                });
    }

    private CommandAPICommand borderSetter(String name, Consumer<WorldBorder> setter) {
        return new CommandAPICommand(name)
                .withArguments(
                        new Location2DArgument("center").setOptional(false),
                        new DoubleArgument("diameter").setOptional(false)
                )
                .executes((sender, args) -> {
                    if(EventManager.main().getCurrentEvent() == null) {
                        this.errorBecauseNoEvent(sender);
                        return;
                    }

                    Location2D center = (Location2D) args.get("center");
                    Double diameter = (Double) args.get("diameter");

                    WorldBorder vWorldBorder = Bukkit.createWorldBorder();

                    if(center == null) {
                        this.errorBecause(sender, "Invalid border center co-ordinates!");
                        return;
                    }

                    vWorldBorder.setCenter(center);

                    if(diameter == null || diameter <= 1 || diameter >= vWorldBorder.getMaxSize()) {
                        this.errorBecause(sender, "Invalid border size!");
                        return;
                    }

                    vWorldBorder.setSize(diameter);

                    setter.accept(vWorldBorder);
                    sender.sendMessage(TextUtil.message("Updated '%s'!".formatted(name)));
                });
    }


    private void errorBecauseNoEvent(CommandSender sender) {
        Component component = Component.text(
                "There is no event currently running / or waiting to be run! Create one with /event new",
                NamedTextColor.RED
        );

        sender.sendMessage(component);
    }

    private void errorBecause(CommandSender sender, String message) {
        Component component = Component.text(
                message,
                NamedTextColor.RED
        );

        sender.sendMessage(component);
    }
}
