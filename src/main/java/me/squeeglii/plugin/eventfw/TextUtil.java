package me.squeeglii.plugin.eventfw;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class TextUtil {

    public static Component message(String message) {
        return Component.text()
                .append(Component.text("E | ", TextColor.color(200, 100, 20)))
                .append(Component.text(message, NamedTextColor.GRAY))
                .build();
    }

}
