package me.squeeglii.plugin.eventfw.config.data;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;

public class StringGetter extends ConfigGetter<String> {

    public StringGetter(String key) {
        super(key);
    }

    @Override
    public Optional<String> from(ConfigurationSection config) {
        if(!config.contains(this.get()))
            return Optional.empty();

        String val = config.getString(this.get());

        if(val == null || val.isEmpty())
            return Optional.empty();

        return Optional.of(val);
    }
}
