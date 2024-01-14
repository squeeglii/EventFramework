package me.squeeglii.plugin.eventfw.command.param;

import me.squeeglii.plugin.eventfw.session.EventManager;

import java.util.function.Predicate;

public class ParamAsserts {

    public static final ParameterAssertion<String> STRING_NOT_EMPTY = simple(str -> str != null && !str.isBlank(), (str, name) -> "Text param is empty for '%s'!".formatted(name));

    public static final ParameterAssertion<Integer> INT_GREATER_THAN_ZERO = simple(num -> num > 0, (val, name) -> "Int param for '%s' must be greater than zero!".formatted(name));

    public static final SimpleParameterAssertion<Object> EVENT_HAS_NOT_STARTED = simple(obj -> !EventManager.main().isEventRunning(), (obj, name) -> "This property cannot be changed while an event is running.");

    public static <V> SimpleParameterAssertion<V> simple(Predicate<V> test, ParameterError<V> errorProvider) {
        return new SimpleParameterAssertion<>(test, errorProvider);
    }

    public static <V> ParameterAssertion<V> wrap(SimpleParameterAssertion<?> assertion) {
        return new SimpleParameterAssertion<>(assertion);
    }
}
