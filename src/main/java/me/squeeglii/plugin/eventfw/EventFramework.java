package me.squeeglii.plugin.eventfw;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPIConfig;
import me.squeeglii.plugin.eventfw.command.ConfiguredCommand;
import me.squeeglii.plugin.eventfw.command.EventCommand;
import me.squeeglii.plugin.eventfw.command.JoinCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.List;

public final class EventFramework extends JavaPlugin {

    private static EventFramework instance;

    private final List<ConfiguredCommand> commands = new LinkedList<>();

    @Override
    public void onLoad() {
        CommandAPIConfig<?> cmdApiCfg = new CommandAPIBukkitConfig(this);
        CommandAPI.onLoad(cmdApiCfg);

        this.registerCommand(new EventCommand());
        this.registerCommand(new JoinCommand());
    }

    @Override
    public void onEnable() {
        instance = this;
        CommandAPI.onEnable();

        this.saveDefaultConfig();


    }

    @Override
    public void onDisable() {
        if(instance == this)
            instance = null;

        for(ConfiguredCommand command: this.commands)
            CommandAPI.unregister(command.getId());

        this.commands.clear();
    }


    private void registerCommand(ConfiguredCommand command) {
        this.commands.add(command);
        command.buildCommand().register();
    }



    public static EventFramework plugin() {
        return instance;
    }

}
