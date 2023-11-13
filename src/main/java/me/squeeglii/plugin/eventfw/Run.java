package me.squeeglii.plugin.eventfw;

public class Run {

    public static void sync(Runnable runnable) {
        EventFramework.plugin()
                .getServer()
                .getScheduler()
                .runTask(EventFramework.plugin(), runnable);
    }

    public static void syncDelayed(int ticks, Runnable runnable) {
        EventFramework.plugin()
                      .getServer()
                      .getScheduler()
                      .scheduleSyncDelayedTask(EventFramework.plugin(), runnable, ticks);
    }


}
