package me.squeeglii.plugin.eventfw.config.data;


import me.squeeglii.plugin.eventfw.EventFramework;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

public abstract class ConfigGetter<T> extends Key<T> {
    public ConfigGetter(String key) {
        super(key);
    }

    public abstract Optional<T> from(ConfigurationSection config);

    public final Optional<T> main() {
        return this.from(EventFramework.plugin().getConfig());
    }

}
