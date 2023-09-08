package io.cwiekala.agregates.domain.bid;

import io.cwiekala.agregates.domain.DomainEvent;
import io.cwiekala.agregates.utils.Command;
import io.cwiekala.agregates.utils.Event;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Event
@Builder
@Value
public class BidWasPlaced implements DomainEvent {

    @NonNull UUID eventId = UUID.randomUUID();
    UUID aggregateId;
    Instant when;

    public static BidWasPlaced now(UUID aggregateId){
        return BidWasPlaced.builder().when(Instant.now())
            .aggregateId(aggregateId)
            .build();
    }

}
