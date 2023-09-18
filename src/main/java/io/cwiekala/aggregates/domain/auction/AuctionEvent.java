package io.cwiekala.aggregates.domain.auction;

import io.cwiekala.aggregates.commons.events.DomainEvent;
import io.cwiekala.aggregates.domain.auction.Auction.AuctionId;
import io.vavr.collection.List;
import io.vavr.control.Option;
import java.time.Instant;
import java.util.UUID;
import lombok.NonNull;
import lombok.Value;
import org.javamoney.moneta.Money;

public interface AuctionEvent extends DomainEvent {

    default AuctionId auctionId() {
        return new AuctionId(getAuctionId());
    }

    UUID getAuctionId();

    default UUID getAggregateId() {
       return getAuctionId();
    }

    @Value
    class AuctionCreated implements AuctionEvent {
        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull Instant when;
        @NonNull UUID auctionId;
        public static AuctionCreated now(AuctionId auctionId) {
            return new AuctionCreated(Instant.now(), auctionId.getValue());
        }
    }

    @Value
    class BidPlaceFailed implements AuctionEvent {
        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull Instant when;
        @NonNull UUID auctionId;

        public static BidPlaceFailed now(AuctionId auctionId) {
            return new BidPlaceFailed(Instant.now(), auctionId.getValue());
        }
    }

    @Value
    class BidWasPlaced implements AuctionEvent {
        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull Instant when;
        @NonNull UUID auctionId;
        @NonNull Money money;

        public static BidWasPlaced now(AuctionId auctionId, Money money) {
            return new BidWasPlaced(Instant.now(), auctionId.getValue(), money);
        }
    }


}



