package me.squeeglii.plugin.eventfw.command.param;

public abstract class ParameterAssertion<V> {

    public abstract boolean test(V valueIn);

    public abstract String getErrorMessage(V valueIn, String parameterName);

}
