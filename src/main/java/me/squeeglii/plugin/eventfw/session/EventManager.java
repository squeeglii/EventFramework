package me.squeeglii.plugin.eventfw.session;

import me.squeeglii.plugin.eventfw.EventFramework;
import org.bukkit.entity.Player;

public class EventManager {

    private EventInstance serverEvent;

    public EventManager() {
        this.serverEvent = null;
    }


    public void setEvent(EventInstance serverEvent) {
        this.serverEvent = serverEvent;
    }

    public void stopCurrentEvent() {
        if(this.serverEvent == null)
            return;

        if(this.serverEvent.hasStarted())
            this.serverEvent.stop();

        this.serverEvent = null;
    }

    public EventInstance getCurrentEvent() {
        return this.serverEvent;
    }


    public boolean isPlayerParticipating(Player player) {
        return this.serverEvent != null &&
               this.serverEvent.getPlayerList()
                               .contains(player); //check player list
    }

    public boolean isEventRunning() {
        return this.serverEvent != null &&
               this.serverEvent.hasStarted();
    }

    public static EventManager main() {
        return EventFramework.plugin().getEventManager();
    }

}
