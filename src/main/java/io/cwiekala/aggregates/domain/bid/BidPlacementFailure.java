package io.cwiekala.aggregates.domain.bid;

import io.cwiekala.aggregates.domain.DomainEvent;
import io.cwiekala.aggregates.utils.Event;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Event
@Builder
@Value
public class BidPlacementFailure implements DomainEvent {

    @NonNull UUID eventId = UUID.randomUUID();
    UUID aggregateId;
    Instant when;

    public static BidPlacementFailure now(UUID aggregateId){
        return BidPlacementFailure.builder().when(Instant.now())
            .aggregateId(aggregateId)
            .build();
    }

}