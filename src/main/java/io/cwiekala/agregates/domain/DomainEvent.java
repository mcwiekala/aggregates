package io.cwiekala.agregates.domain;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {

    UUID getEventId();

    UUID getAggregateId();

    Instant getWhen();
}
