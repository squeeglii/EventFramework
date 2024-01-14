package me.squeeglii.plugin.eventfw.command.param;

import java.util.function.Predicate;

public class SimpleParameterAssertion<V> extends ParameterAssertion<V> {

    private final Predicate<V> test;
    private final ParameterError<V> errorProvider;

    public SimpleParameterAssertion(Predicate<V> test, ParameterError<V> errorProvider) {
        this.test = test;
        this.errorProvider = errorProvider;
    }

    @Override
    public boolean test(V valueIn) {
        return this.test.test(valueIn);
    }

    @Override
    public String getErrorMessage(V valueIn, String parameterName) {
        return this.errorProvider.getErrorFor(valueIn, parameterName);
    }

}
