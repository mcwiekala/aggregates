package io.cwiekala.aggregates;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;
import java.util.Optional;
import lombok.ToString;

@ToString
class EventChecker {

    private List<Object> events;

    EventChecker(List<Object> events) {
        this.events = events;
    }

    public Object assertEventAndPop(Class clazz) {
        Optional<Object> foundEvent = events.stream().filter(event -> event.getClass().equals(clazz)).findAny();
        assertThat(foundEvent).isPresent();
        events.remove(foundEvent.orElseThrow());
        return foundEvent;
    }
}
