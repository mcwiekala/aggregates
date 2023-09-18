package io.cwiekala.aggregates.infrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.ToString;
import org.springframework.context.ApplicationEventPublisher;

@ToString
public class InMemoryEventPublisher implements ApplicationEventPublisher {

    List<Object> events = new ArrayList<>();

    @Override
    public void publishEvent(Object event) {
        events.add(event);
    }

    public List<Object> getEvents() {
        return events;
    }

}
