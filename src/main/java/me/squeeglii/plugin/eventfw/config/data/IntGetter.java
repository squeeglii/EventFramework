package me.squeeglii.plugin.eventfw.config.data;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

public class IntGetter extends ConfigGetter<Integer> {

    public IntGetter(String key) {
        super(key);
    }

    @Override
    public Optional<Integer> from(ConfigurationSection config) {
        if(!config.contains(this.get()))
            return Optional.empty();

        int val = config.getInt(this.get());

        return Optional.of(val);
    }
}
