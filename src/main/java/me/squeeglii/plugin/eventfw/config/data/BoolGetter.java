package me.squeeglii.plugin.eventfw.config.data;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

public class BoolGetter extends ConfigGetter<Boolean> {

    public BoolGetter(String key) {
        super(key);
    }

    @Override
    public Optional<Boolean> from(ConfigurationSection config) {
        if(!config.contains(this.get()))
            return Optional.empty();

        boolean val = config.getBoolean(this.get());

        return Optional.of(val);
    }
}
