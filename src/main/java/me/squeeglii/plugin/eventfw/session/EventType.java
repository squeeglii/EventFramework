package me.squeeglii.plugin.eventfw.session;

import me.squeeglii.plugin.eventfw.session.type.DebugEvent;
import me.squeeglii.plugin.eventfw.session.type.DynamicBuildEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class EventType {

    private static final HashMap<String, Supplier<EventInstance>> registeredTypes;

    static {
        registeredTypes = new HashMap<>();
        registeredTypes.put("open_build", DynamicBuildEvent::new);
        registeredTypes.put("debug", DebugEvent::new);
    }


    public static Optional<Supplier<EventInstance>> get(String typeId) {
        String checkedTypeId = typeId.trim().toLowerCase();
        Supplier<EventInstance> val = registeredTypes.get(checkedTypeId);
        return Optional.ofNullable(val);
    }

    public static Optional<EventInstance> getAndCreate(String typeId) {
        Optional<Supplier<EventInstance>> factory = EventType.get(typeId);

        if(factory.isEmpty())
            return Optional.empty();

        EventInstance instance = factory.get().get();

        return Optional.of(instance);
    }

    public static Set<String> allTypeIds() {
        return Collections.unmodifiableSet(registeredTypes.keySet());
    }
}
