package me.squeeglii.plugin.eventfw.config.data;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

public class LongGetter extends ConfigGetter<Long> {

    public LongGetter(String key) {
        super(key);
    }

    @Override
    public Optional<Long> from(ConfigurationSection config) {
        if(!config.contains(this.get()))
            return Optional.empty();

        long val = config.getLong(this.get());

        return Optional.of(val);
    }
}
