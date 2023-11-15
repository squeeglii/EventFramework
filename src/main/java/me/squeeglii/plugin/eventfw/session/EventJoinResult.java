package me.squeeglii.plugin.eventfw.session;

public enum EventJoinResult {

    SUCCESS,
    PLAYER_ALREADY_IN_EVENT,
    EVENT_FULL,
    EVENT_HAS_NOT_STARTED,

    // Not an exception but still a problem - when addPlayer returns false but all checks have passed.
    UNHANDLED_FAILURE,

    // Exceptions :)
    ERROR

}
