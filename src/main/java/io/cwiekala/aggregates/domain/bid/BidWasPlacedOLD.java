package io.cwiekala.aggregates.domain.bid;

import io.cwiekala.aggregates.commons.events.DomainEvent;
import io.cwiekala.aggregates.domain.auction.Auction.AuctionId;
import io.cwiekala.aggregates.utils.Event;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.javamoney.moneta.Money;

@Event
@Builder
@Value
public class BidWasPlacedOLD implements DomainEvent {

    @NonNull UUID eventId = UUID.randomUUID();
    UUID aggregateId;
    Instant when;
    Money money;

    public static BidWasPlacedOLD now(AuctionId aggregateId, Money money){
        return BidWasPlacedOLD.builder().when(Instant.now())
            .aggregateId(aggregateId.getValue())
            .money(money)
            .build();
    }

}
