package me.squeeglii.plugin.eventfw.config.data;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Optional;

public class StringListGetter extends ConfigGetter<List<String>> {

    public StringListGetter(String key) {
        super(key);
    }

    @Override
    public Optional<List<String>> from(ConfigurationSection config) {
        if(!config.contains(this.get()))
            return Optional.empty();

        List<String> val = config.getStringList(this.get());

        return Optional.of(val);
    }

}
