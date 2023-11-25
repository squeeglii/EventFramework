package me.squeeglii.plugin.eventfw;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPIConfig;
import me.squeeglii.plugin.eventfw.command.ConfiguredCommand;
import me.squeeglii.plugin.eventfw.command.EventCommand;
import me.squeeglii.plugin.eventfw.command.JoinCommand;
import me.squeeglii.plugin.eventfw.session.EventManager;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.List;

public final class EventFramework extends JavaPlugin {

    private static EventFramework instance = null;

    private final List<ConfiguredCommand> commands = new LinkedList<>();

    private EventManager sessionTracker;


    @Override
    public void onLoad() {
        instance = this;

        this.saveDefaultConfig();

        this.sessionTracker = new EventManager(); // commands need access

        CommandAPIConfig<?> cmdApiCfg = new CommandAPIBukkitConfig(this);
        CommandAPI.onLoad(cmdApiCfg);

        this.registerCommand(new JoinCommand());
    }

    @Override
    public void onEnable() {
        instance = this; // sanity check?

        this.getServer()
                .getPluginManager()
                .registerEvents(new ActivityListener(), this);

        CommandAPI.onEnable();

        // Needs to be loaded post-world
        this.registerCommand(new EventCommand());
    }

    @Override
    public void onDisable() {
        for(ConfiguredCommand command: this.commands)
            CommandAPI.unregister(command.getId());

        this.commands.clear();

        if(instance == this)
            instance = null;
    }


    private void registerCommand(ConfiguredCommand command) {
        this.commands.add(command);
        command.buildCommand().register();
    }


    public EventManager getEventManager() {
        return this.sessionTracker;
    }

    public static EventFramework plugin() {
        return instance;
    }

    public void registerListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    public void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

}
