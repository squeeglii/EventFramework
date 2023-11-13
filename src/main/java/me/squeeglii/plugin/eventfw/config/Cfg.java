package me.squeeglii.plugin.eventfw.config;

import me.squeeglii.plugin.eventfw.config.data.BoolGetter;

public class Cfg {

    public static final BoolGetter OVERRIDE_JOIN_MESSAGE = new BoolGetter("override-join-message");
    public static final BoolGetter ENABLE_JOIN_EVENT_HINT = new BoolGetter("enable-event-join-push");

}
