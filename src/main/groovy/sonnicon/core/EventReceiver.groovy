package sonnicon.core

import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import sonnicon.type.EventType

class EventReceiver implements EventListener {

    @Override
    void onEvent(GenericEvent event) {
        Events.fire(EventType.valueOf(event.getClass().getSimpleName()), event)
    }
}
