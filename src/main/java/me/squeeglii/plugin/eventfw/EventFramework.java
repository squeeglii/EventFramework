package me.squeeglii.plugin.eventfw;

import me.lucko.commodore.CommodoreProvider;
import me.squeeglii.plugin.eventfw.command.ConfiguredCommand;
import me.squeeglii.plugin.eventfw.command.EventCommand;
import me.squeeglii.plugin.eventfw.command.JoinCommand;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class EventFramework extends JavaPlugin {

    private static EventFramework instance;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

        this.registerCommand(new EventCommand());
        this.registerCommand(new JoinCommand());
    }

    @Override
    public void onDisable() {
        if(instance == this)
            instance = null;
    }


    private void registerCommand(ConfiguredCommand command) {
        PluginCommand cmd = this.getCommand(command.getId());

        if(cmd == null) {
            this.getLogger().warning("Could not find command '%s'".formatted(command.getId()));
            return;
        }

        cmd.setExecutor(command);

        if (CommodoreProvider.isSupported()) {
            CommodoreProvider.getCommodore(this)
                             .register(cmd, command.configureTabCompletion());
        }
    }



    public static EventFramework plugin() {
        return instance;
    }

}
