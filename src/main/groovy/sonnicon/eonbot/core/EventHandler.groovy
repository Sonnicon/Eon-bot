package sonnicon.eonbot.core

import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import org.jetbrains.annotations.NotNull

/**
 * JDA EventListener event proxy registration system.
 */
class EventHandler implements EventListener {
    /**
     * Map of event classes and associated handlers.
     */
    static protected Map<Class<GenericEvent>, ArrayList<Closure>> events = [:]
    /**
     * Event that is currently being handled.
     */
    static protected Class<GenericEvent> invoking = null
    /**
     * Events that will be registered and unregistered when current invocation finishes.
     */
    static protected ArrayList<Closure> toRegister = [], toRemove = []

    /**
     * Register a handler for an event, starting from next invocation of the event.
     * @param event Event to trigger on
     * @param closure Handler to call on event
     */
    static void register(Class<GenericEvent> event, Closure closure) {
        if (event == invoking) {
            // No event concurrent modification
            toRegister.add(closure)
        } else {
            // Add to map if safe to add
            events.putIfAbsent(event, [])
            events.get(event).add(closure)
        }
    }

    /**
     * Unregister a handler for an event, will still execute during current invocation.
     * @param event Event to remove handler from
     * @param closure Handler to be removed
     */
    static void remove(Class<GenericEvent> event, Closure closure) {
        if (event == invoking) {
            // No event concurrent modification
            toRemove.add(closure)
        } else {
            // Remove from map if safe to remove
            ArrayList<Closure> list = events.get(event)
            if (list != null) {
                list.remove(closure)
                if (list.isEmpty()) {
                    events.remove(event)
                }
            }
        }
    }

    /**
     * Fire associated handlers for an event.
     * @param event The Event to handle
     */
    @Override
    void onEvent(@NotNull GenericEvent event) {
        ArrayList<Closure> list = events.get(event.class)
        // Are there actually any events?
        if (list != null) {
            // Call everything
            invoking = event.class
            list.each { it.call(event) }
            invoking = null
            // Update changes for future invocations
            toRegister.each { register(event.class, it) }.clear()
            toRemove.each { remove(event.class, it) }.clear()
        }
    }
}
