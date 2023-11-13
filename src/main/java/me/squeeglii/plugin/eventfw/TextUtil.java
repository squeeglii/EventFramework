package me.squeeglii.plugin.eventfw;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class TextUtil {

    public static final TextColor BRANDING = TextColor.color(200, 100, 20);

    public static Component message(String message) {
        return Component.text()
                .append(Component.text("E | ", BRANDING))
                .append(Component.text(message, NamedTextColor.GRAY))
                .build();
    }

}
