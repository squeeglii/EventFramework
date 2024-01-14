package me.squeeglii.plugin.eventfw.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.wrappers.Location2D;
import me.squeeglii.plugin.eventfw.EventFramework;
import me.squeeglii.plugin.eventfw.Permission;
import me.squeeglii.plugin.eventfw.TextUtil;
import me.squeeglii.plugin.eventfw.command.param.ParamAsserts;
import me.squeeglii.plugin.eventfw.command.param.ParameterAssertion;
import me.squeeglii.plugin.eventfw.session.EventInstance;
import me.squeeglii.plugin.eventfw.session.EventManager;
import me.squeeglii.plugin.eventfw.session.EventType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

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
                        this.borderSetter(EventInstance::setAreaBounds),
                        this.stringSetter("name", EventInstance::setName, ParamAsserts.STRING_NOT_EMPTY),
                        this.stringSetter("description", EventInstance::setDescription),
                        this.intSetter("player_limit", EventInstance::setPlayerLimit, ParamAsserts.INT_GREATER_THAN_ZERO),
                        this.boolSetter("announce_event_start", EventInstance::setShouldAnnounceEvent),
                        this.worldSetter("dimension", EventInstance::setHostingWorldId),
                        this.boolSetter("prevent_dimension_switches", EventInstance::setShouldPreventDimensionSwitching),
                        this.locationSetter("spawnpoint", EventInstance::setSpawn),
                        this.boolSetter("disable_ender_chests", EventInstance::setDisableEnderChests),
                        this.boolSetter("disable_player_drops", EventInstance::setDisablePlayerDrops),
                        this.boolSetter("use_temporary_players", EventInstance::setUseTemporaryPlayers)
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

    @SafeVarargs
    private CommandAPICommand intSetter(String name, BiConsumer<EventInstance, Integer> setter, ParameterAssertion<Integer>... assertions) {
        return new CommandAPICommand(name)
                .withArguments(new IntegerArgument("value").setOptional(false))
                .executes((sender, args) -> {
                    Integer value = (Integer) args.get("value");

                    if(value == null) {
                        this.errorBecause(sender, "Missing integer param for 'value'!");
                        return;
                    }

                    for(ParameterAssertion<Integer> assertion: assertions) {
                        if(assertion.test(value)) continue;

                        String errorMessage = assertion.getErrorMessage(value, "value");
                        this.errorBecause(sender, errorMessage);
                        return;
                    }

                    this.complete(sender, setter, name, value);
                });
    }

    @SafeVarargs
    private CommandAPICommand boolSetter(String name, BiConsumer<EventInstance, Boolean> setter, ParameterAssertion<Boolean>... assertions) {
        return new CommandAPICommand(name)
                .withArguments(new BooleanArgument("value").setOptional(false))
                .executes((sender, args) -> {
                    Boolean value = (Boolean) args.get("value");

                    if(value == null) {
                        this.errorBecause(sender, "Missing boolean param for 'value'!");
                        return;
                    }

                    // tbf, are boolean assertions needed? Guess you could check the state of something?
                    for(ParameterAssertion<Boolean> assertion: assertions) {
                        if(assertion.test(value)) continue;

                        String errorMessage = assertion.getErrorMessage(value, "value");
                        this.errorBecause(sender, errorMessage);
                        return;
                    }

                    this.complete(sender, setter, name, value);
                });
    }

    @SafeVarargs
    private CommandAPICommand stringSetter(String name, BiConsumer<EventInstance, String> setter, ParameterAssertion<String>... assertions) {
        return new CommandAPICommand(name)
                .withArguments(new GreedyStringArgument("value").setOptional(false))
                .executes((sender, args) -> {
                    String value = (String) args.get("value");

                    if(value == null) {
                        this.errorBecause(sender, "Missing text param for 'value'!");
                        return;
                    }

                    for(ParameterAssertion<String> assertion: assertions) {
                        if(assertion.test(value)) continue;

                        String errorMessage = assertion.getErrorMessage(value, "value");
                        this.errorBecause(sender, errorMessage);
                        return;
                    }

                    this.complete(sender, setter, name, value);
                });
    }

    @SafeVarargs
    private CommandAPICommand locationSetter(String name, BiConsumer<EventInstance, Location> setter, ParameterAssertion<Location>... assertions) {
        return new CommandAPICommand(name)
                .withArguments(new LocationArgument("value").setOptional(false))
                .executes((sender, args) -> {

                    Location value = (Location) args.get("value");

                    if(value == null) {
                        this.errorBecause(sender, "Missing location param for 'value'!");
                        return;
                    }

                    for(ParameterAssertion<Location> assertion: assertions) {
                        if(assertion.test(value)) continue;

                        String errorMessage = assertion.getErrorMessage(value, "value");
                        this.errorBecause(sender, errorMessage);
                        return;
                    }

                    EventInstance event = EventManager.main().getCurrentEvent();

                    if(event == null) {
                        this.errorBecauseNoEvent(sender);
                        return;
                    }

                    setter.accept(event, value);
                    sender.sendMessage(TextUtil.message("Updated '%s' to 'x=%s, y = %s, z=%s'!".formatted(
                            name, value.x(), value.y(), value.z()
                    )));
                });
    }

    private CommandAPICommand borderSetter(BiConsumer<EventInstance, WorldBorder> setter) {
        return new CommandAPICommand("border")
                .withArguments(
                        new Location2DArgument("center").setOptional(false),
                        new DoubleArgument("diameter").setOptional(false)
                )
                .executes((sender, args) -> {
                    Location2D center = (Location2D) args.get("center");
                    Double diameter = (Double) args.get("diameter");

                    WorldBorder vWorldBorder = Bukkit.createWorldBorder();

                    if(center == null) {
                        this.errorBecause(sender, "Incomplete co-ordinates for border 'center'!");
                        return;
                    }

                    if(diameter == null) {
                        this.errorBecause(sender, "Invalid value for border 'diameter'!");
                        return;
                    }

                    if(diameter <= 1) {
                        this.errorBecause(sender, "Value for border 'diameter' should not be smaller than 1!");
                        return;
                    }

                    double maxSize = vWorldBorder.getMaxSize();

                    if(diameter >= maxSize) {
                        this.errorBecause(
                                sender,
                                "Value for border 'diameter' must not be bigger than or equal to %s!".formatted(maxSize)
                        );
                        return;
                    }

                    EventInstance event = EventManager.main().getCurrentEvent();

                    if(event == null) {
                        this.errorBecauseNoEvent(sender);
                        return;
                    }

                    vWorldBorder.setCenter(center);
                    vWorldBorder.setSize(diameter);

                    setter.accept(event, vWorldBorder);
                    sender.sendMessage(TextUtil.message("Updated 'border'!"));
                });
    }

    private CommandAPICommand worldSetter(String name, BiConsumer<EventInstance, NamespacedKey> setter) {
        CommandAPICommand command = new CommandAPICommand(name);

        for(World world: EventFramework.plugin().getServer().getWorlds()) {
            NamespacedKey val = world.getKey();
            String strVal = val.asString();

            command.withSubcommand(new CommandAPICommand(strVal).executes((sender, args) -> {

                EventInstance event = EventManager.main().getCurrentEvent();

                if(event == null) {
                    this.errorBecauseNoEvent(sender);
                    return;
                }

                setter.accept(event, val);
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

    private <V> void complete(CommandSender sender, BiConsumer<EventInstance, V> setter, String argName, V value) {
        EventInstance event = EventManager.main().getCurrentEvent();

        if(EventManager.main().getCurrentEvent() == null) {
            this.errorBecauseNoEvent(sender);
            return;
        }

        setter.accept(event, value);
        sender.sendMessage(TextUtil.message("Updated '%s' to '%s'!".formatted(argName, value)));
    }
}
