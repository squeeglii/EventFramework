package me.squeeglii.plugin.eventfw;

import org.bukkit.scoreboard.Scoreboard;

public class SpigotUtil {

    public static Scoreboard getServerScoreboard() {
        return EventFramework.plugin()
                .getServer()
                .getScoreboardManager()
                .getMainScoreboard();
    }

}
