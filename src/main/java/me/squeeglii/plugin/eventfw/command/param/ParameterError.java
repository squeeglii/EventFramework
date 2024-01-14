package me.squeeglii.plugin.eventfw.command.param;

@FunctionalInterface
public interface ParameterError<V> {

    String getErrorFor(V value, String parameterName);

}
