package sonnicon.eonbot.core

import net.dv8tion.jda.api.events.GenericEvent
import sonnicon.eonbot.type.EventType

import java.util.function.Consumer

class Events {
    private static Map<EventType, Map<String, Consumer>> events = [:]

    private static tmp = []

    static fire(EventType type, GenericEvent event) {
        if (!events.containsKey(type)) return
        tmp = events.get(type).collect()
        tmp.each { cons -> cons.value.accept(event) }
    }

    static on(EventType type, Consumer<GenericEvent> cons) {
        on(type, cons, Modules.moduleName)
    }

    static on(EventType type, Consumer<GenericEvent> cons, String module) {
        if (!events.containsKey(type))
            events.put(type, [:])
        events.get(type).put(module, cons)
    }

    static remove(String module) {
        tmp.clear()
        events.each { entry ->
            if (entry.value.containsKey(module)) {
                entry.value.remove(module)
                if (entry.value.size() == 0)
                    tmp.add(entry.key)
            }
        }
        tmp.each { key -> events.remove(key) }
    }
}
