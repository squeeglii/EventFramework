package me.squeeglii.plugin.eventfw.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.wrappers.Location2D;
import me.squeeglii.plugin.eventfw.EventFramework;
import me.squeeglii.plugin.eventfw.Permission;
import me.squeeglii.plugin.eventfw.TextUtil;
import me.squeeglii.plugin.eventfw.session.EventInstance;
import me.squeeglii.plugin.eventfw.session.EventManager;
import me.squeeglii.plugin.eventfw.session.EventType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class EventCommand extends ConfiguredCommand {

    public static final String MANAGE_EVENT = "eventfw.manage";

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

        cmd.withPermission(Permission.MANAGE_EVENT);
        cmd.setSubcommands(List.of(
                this.getCreateCommand(),
                this.getStopCommand(),
                this.getConfigCommand(),
                this.getLaunchCommand()
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
                .executes((sender, args) -> {

                    if(!EventManager.main().isEventRunning()) {
                        Component errMsg = Component.text("There are no events currently running!");
                        sender.sendMessage(errMsg);
                        return;
                    }

                    EventManager.main().stopCurrentEvent();

                    Component component = TextUtil.message("Stopped any running events.");
                    sender.sendMessage(component);
                });
    }

    protected CommandAPICommand getConfigCommand() {
        return new CommandAPICommand("configure")
                .withSubcommands(
                        this.stringSetter("name", val -> EventManager.main().getCurrentEvent().setName(val)),
                        this.stringSetter("description", val -> EventManager.main().getCurrentEvent().setDescription(val)),
                        this.intSetter("player_limit", val -> EventManager.main().getCurrentEvent().setPlayerLimit(val)),
                        this.borderSetter("border", val -> EventManager.main().getCurrentEvent().setAreaBounds(val)),
                        this.boolSetter("announce_event_start", val -> EventManager.main().getCurrentEvent().setShouldAnnounceEvent(val)),
                        this.worldSetter("dimension", val -> EventManager.main().getCurrentEvent().setHostingWorldId(val)),
                        this.boolSetter("prevent_dimension_switches", val -> EventManager.main().getCurrentEvent().setShouldPreventDimensionSwitching(val)),
                        this.locationSetter("spawnpoint", val -> EventManager.main().getCurrentEvent().setSpawn(val)),
                        this.boolSetter("disable_ender_chests", val -> EventManager.main().getCurrentEvent().setDisableEnderChests(val)),
                        this.boolSetter("disable_player_drops", val -> EventManager.main().getCurrentEvent().setDisablePlayerDrops(val)),
                        this.boolSetter("use_temporary_players", val -> EventManager.main().getCurrentEvent().setUseTemporaryPlayers(val))
                );
    }

    protected CommandAPICommand getLaunchCommand() {
        return new CommandAPICommand("launch")
                .executes((sender, args) -> {
                    EventInstance event = EventManager.main().getCurrentEvent();

                    if(event == null) {
                        sender.sendMessage(Component.text("There's no pending events! Create one with /event new.", NamedTextColor.RED));
                        return;
                    }

                    if(event.hasStarted()) {
                        sender.sendMessage(Component.text("This event has already started.", NamedTextColor.RED));
                        return;
                    }

                    try {
                        event.start();
                    } catch (Exception err) {
                        sender.sendMessage(Component.text("Something went wrong while starting the event.", NamedTextColor.RED));
                        EventFramework.plugin().getLogger().throwing("EventCommand", "launch", err);
                        return;
                    }

                    Component msg = TextUtil.message("Successfully started the event! Join it with /join.");
                    sender.sendMessage(msg);
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

    private CommandAPICommand boolSetter(String name, Consumer<Boolean> setter) {
        return new CommandAPICommand(name)
                .withArguments(new BooleanArgument("value").setOptional(false))
                .executes((sender, args) -> {
                    if(EventManager.main().getCurrentEvent() == null) {
                        this.errorBecauseNoEvent(sender);
                        return;
                    }

                    Boolean value = (Boolean) args.get("value");

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

    private CommandAPICommand locationSetter(String name, Consumer<Location> setter) {
        return new CommandAPICommand(name)
                .withArguments(new LocationArgument("value").setOptional(false))
                .executes((sender, args) -> {
                    if(EventManager.main().getCurrentEvent() == null) {
                        this.errorBecauseNoEvent(sender);
                        return;
                    }

                    Location value = (Location) args.get("value");

                    if(value == null) {
                        this.errorBecause(sender, "Invalid value!");
                        return;
                    }

                    setter.accept(value);
                    sender.sendMessage(TextUtil.message("Updated '%s' to 'x=%s, y = %s, z=%s'!".formatted(
                            name, value.x(), value.y(), value.z()
                    )));
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

    private CommandAPICommand worldSetter(String name, Consumer<NamespacedKey> setter) {
        CommandAPICommand command = new CommandAPICommand(name);

        for(World world: EventFramework.plugin().getServer().getWorlds()) {
            NamespacedKey val = world.getKey();
            String strVal = val.asString();

            command.withSubcommand(new CommandAPICommand(strVal).executes((sender, args) -> {
                setter.accept(val);
                sender.sendMessage(TextUtil.message("Updated '%s' to '%s'!".formatted(name, strVal)));
            }));
        }

        return command;
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
