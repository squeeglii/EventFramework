package me.squeeglii.plugin.eventfw;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;

public class TextUtil {

    public static final TextColor BRANDING = TextColor.color(255, 160, 20);

    public static Component message(String message) {
        return Component.text()
                .append(Component.text("Events #  ", BRANDING))
                .append(Component.text(message, NamedTextColor.WHITE))
                .build();
    }

    public static void send(String message, CommandSender recipient) {
        Component msg = TextUtil.message(message);
        recipient.sendMessage(msg);
    }

}
