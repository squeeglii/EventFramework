package me.squeeglii.plugin.eventfw.session;

import me.squeeglii.plugin.eventfw.exception.InvalidConfigurationException;
import org.bukkit.entity.Player;

/**
 * The set of methods intended to be overwritten by an implementor.
 * Moved into their own class for clarity.
 */
public interface EventAPI {

    /** Gives the Event a chance to approve/disapprove of a player joining. */
    default boolean onPlayerJoinTest(Player player) {
        return true;
    }

    default void onPrePlayerAdd(Player player) { }

    void onPlayerAdd(Player player);

    void onPlayerLeave(Player player);

    void onTick();

    void onStart() throws InvalidConfigurationException;

    void onStop();

}
