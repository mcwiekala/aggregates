package io.cwiekala.aggregates.commons.events;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public interface DomainEvent {

    UUID getEventId();

    UUID getAggregateId();

    LocalDateTime getEventTime();
}
