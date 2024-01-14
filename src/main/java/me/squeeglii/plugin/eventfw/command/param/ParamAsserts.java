package me.squeeglii.plugin.eventfw.command.param;

import java.util.function.Predicate;

public class ParamAsserts {

    public static final ParameterAssertion<String> STRING_NOT_EMPTY = simple(str -> str != null && !str.isBlank(), (str, name) -> "Text param is empty for '%s'!".formatted(name));

    public static final ParameterAssertion<Integer> INT_GREATER_THAN_ZERO = simple(num -> num > 0, (str, name) -> "Int param for '%s' must be greater than zero!".formatted(name));



    private static <V> ParameterAssertion<V> simple(Predicate<V> test, ParameterError<V> errorProvider) {
        return new SimpleParameterAssertion<>(test, errorProvider);
    }
}
