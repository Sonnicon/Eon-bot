package sonnicon.eonbot.core

import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import org.jetbrains.annotations.NotNull

class EventHandler implements EventListener {
    static protected Map<Class<GenericEvent>, ArrayList<Closure>> events = [:]

    static protected Class<GenericEvent> invoking = null
    static protected ArrayList<Closure> toRegister = [], toRemove = []

    static void register(Class<GenericEvent> event, Closure closure) {
        if (event == invoking) {
            toRegister.add(closure)
        } else {
            events.putIfAbsent(event, [])
            events.get(event).add(closure)
        }
    }

    static void remove(Class<GenericEvent> event, Closure closure) {
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
    void onEvent(@NotNull GenericEvent event) {
        ArrayList<Closure> list = events.get(event.class)
        if (list != null) {
            invoking = event.class
            list.each { it.call(event) }
            invoking = null
            toRegister.each { register(event.class, it) }.clear()
            toRemove.each { remove(event.class, it) }.clear()
        }
    }
}
