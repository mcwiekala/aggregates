package io.cwiekala.aggregates.domain.auction;

import io.cwiekala.aggregates.commons.events.DomainEvent;
import io.cwiekala.aggregates.domain.auction.Auction.AuctionId;
import io.cwiekala.aggregates.utils.aggregateid.AuctioneerId;
import io.cwiekala.aggregates.utils.aggregateid.ListingId;
import io.cwiekala.aggregates.utils.aggregateid.SellerId;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.javamoney.moneta.Money;

public interface AuctionEvent extends DomainEvent {

    ////    default AuctionId auctionId() {
////        return new AuctionId(getAuctionId());
////    }
//
    AuctionId getAuctionId();

    default UUID getAggregateId() {
        return getAuctionId().getValue();
    }

    @Value
    class AuctionCreated implements AuctionEvent {

        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull LocalDateTime eventTime;
        @NonNull AuctionId auctionId;
        @NonNull ListingId listingId;
        @NonNull SellerId sellerId;

        public static AuctionCreated now(AuctionId auctionId, ListingId listingId, SellerId sellerId) {
            return new AuctionCreated(LocalDateTime.now(), auctionId, listingId, sellerId);
        }
    }

//    @Value
//    class BidPlaceFailed implements AuctionEvent {
//
//        @NonNull UUID eventId = UUID.randomUUID();
//        @NonNull LocalDateTime eventTime;
//        @NonNull AuctionId auctionId;
//
//        public static BidPlaceFailed now(AuctionId auctionId) {
//            return new BidPlaceFailed(LocalDateTime.now(), auctionId);
//        }
//    }

    @Value
    @Builder
    class BidWasPlaced implements AuctionEvent {

        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull AuctionId auctionId;
        @NonNull AuctioneerId auctioneerId;
        @NonNull LocalDateTime eventTime;
        @NonNull Money newPrice;

        public static BidWasPlaced now(AuctionId auctionId, AuctioneerId auctioneerId, Money newPrice) {
            return new BidWasPlaced(auctionId, auctioneerId, LocalDateTime.now(), newPrice);
        }

        public static BidWasPlaced create(AuctionId auctionId, AuctioneerId auctioneerId, Money newPrice, LocalDateTime time) {
            return new BidWasPlaced(auctionId, auctioneerId, time, newPrice);
        }
    }

    @Value
    @Builder
    class WinningBidWasUpdated implements AuctionEvent {

        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull AuctionId auctionId;
        @NonNull AuctioneerId auctioneerId;
        @NonNull LocalDateTime eventTime;
        @NonNull Money newPrice;

        public static BidWasPlaced now(AuctionId auctionId, AuctioneerId auctioneerId, Money newPrice) {
            return new BidWasPlaced(auctionId, auctioneerId, LocalDateTime.now(), newPrice);
        }
    }

    @Value
    @Builder
    class WinningBidWasChangedWithNewOne implements AuctionEvent {

        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull AuctionId auctionId;
        @NonNull AuctioneerId auctioneerId;
        @NonNull LocalDateTime eventTime;
        @NonNull Money newPrice;

        public static BidWasPlaced now(AuctionId auctionId, AuctioneerId auctioneerId, Money newPrice) {
            return new BidWasPlaced(auctionId, auctioneerId, LocalDateTime.now(), newPrice);
        }
    }

    @Value
        // TODO: Success/Failure Wrapper?
    class BidPlacementFailure implements AuctionEvent {

        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull AuctionId auctionId;
        @NonNull LocalDateTime eventTime;
        @NonNull AuctioneerId auctioneerId; // TODO: to class ID?
        @NonNull String reason;

        public static BidPlacementFailure now(AuctionId auctionId, AuctioneerId auctioneerId, String reason) {
            return new BidPlacementFailure(
                auctionId,
                LocalDateTime.now(),
                auctioneerId,
                reason);
        }

    }

}



