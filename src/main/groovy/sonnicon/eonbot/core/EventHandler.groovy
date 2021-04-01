package sonnicon.eonbot.core

import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import org.jetbrains.annotations.NotNull
import sonnicon.eonbot.type.EventType

import javax.annotation.Nonnull

class EventHandler implements EventListener {
    static protected Map<EventType, ArrayList<Closure>> events = [:]

    static protected EventType invoking = null
    static protected ArrayList<Closure> toRegister = [], toRemove = []

    static void register(EventType event, Closure closure) {
        if (event == invoking) {
            toRegister.add(closure)
        } else {
            events.putIfAbsent(event, [])
            events.get(event).add(closure)
        }
    }

    static void remove(EventType event, Closure closure) {
        if (event == invoking) {
            toRemove.add(closure)
        } else {
            ArrayList<Closure> list = events.get(event)
            if (list != null) {
                list.remove(closure)
            }
        }
    }

    @Override
    void onEvent(@NotNull @Nonnull GenericEvent event) {
        String className = event.getClass().getSimpleName()
        EventType type = EventType.valueOf("on" + className.substring(0, className.length() - 5))
        ArrayList<Closure> list = events.get(type)
        if (list != null) {
            invoking = type
            list.each { it.call(event) }
            invoking = null
            toRegister.each { register(type, it) }.clear()
            toRemove.each { remove(type, it) }.clear()
        }
    }
}
