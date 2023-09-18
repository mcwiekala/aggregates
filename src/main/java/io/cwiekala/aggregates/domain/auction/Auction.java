package io.cwiekala.aggregates.domain.auction;

import static io.cwiekala.aggregates.commons.events.EitherResult.announceFailure;

import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidPlacementFailure;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidWasPlaced;
import io.cwiekala.aggregates.application.command.CreateAuctionCommand;
import io.cwiekala.aggregates.application.command.UpdateAuction;
import io.cwiekala.aggregates.utils.AggregateRoot;
import io.cwiekala.aggregates.utils.aggregateid.AuctioneerId;
import io.cwiekala.aggregates.utils.aggregateid.ListingId;
import io.cwiekala.aggregates.utils.aggregateid.SellerId;
import io.vavr.control.Either;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.javamoney.moneta.Money;

@AggregateRoot
@Getter
@ToString
public class Auction {

    @EmbeddedId
    private AuctionId id;
    LocalDateTime startDate;
    LocalDateTime endDate;
    ListingId listingId;
    SellerId sellerId;
    Money startingPrice;
    WinningBid winningBid;
    Optional<Money> minimalSellingPrice; // TODO??? AuctionConfiguration?

    public Auction(AuctionId id, Duration auctionLength, SellerId sellerId, ListingId listingId, Money startingPrice) {
        this.id = id;
        LocalDateTime now = LocalDateTime.now();
        this.startDate = now; // TODO: as params?
        this.endDate = now.plus(auctionLength);
        this.listingId = listingId;
        this.sellerId = sellerId;
        this.startingPrice = startingPrice;
        this.minimalSellingPrice = Optional.empty();
    }

    // questions and answers?

    public void handle(CreateAuctionCommand command) {
        //
    }

    public Either<BidPlacementFailure, AuctionEvent> handle(BidWasPlaced event) {
        // check end date
        LocalDateTime eventTime = event.getEventTime();
        if (doesEventHappenWhenAuctionIsActive(eventTime)) { // TODO: atomic invariant
            return announceFailure(
                BidPlacementFailure.now(event.getAuctionId(), event.getAuctioneerId(),
                    "Bid was placed when the auction was inactive"));
        }
        if (isNewOfferGreaterThanStartingPrice(event)) {
            winningBid = new WinningBid(startingPrice, event.getNewPrice(), event.getAuctioneerId(),
                event.getEventTime());
        }
        if (minimalSellingPrice.isPresent()
            && event.getNewPrice().compareTo(minimalSellingPrice.get()) > 0) {

        }
        return winningBid.processNewOffer(event);
    }

    private boolean isNewOfferGreaterThanStartingPrice(BidWasPlaced event) {
        return event.getNewPrice().compareTo(startingPrice) > 0;
    }

    private boolean doesEventHappenWhenAuctionIsActive(LocalDateTime eventTime) {
        return eventTime.isAfter(startDate)
            && eventTime.isBefore(eventTime);
    }

    public void handle(UpdateAuction command) {
        // what can be updated?
        // shipment type and payment can be only added
    }

    public Money getActualPrice() {
        return winningBid.getActualPrice();
    }

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Data
    public static class AuctionId {

        @Column(name = "id", columnDefinition = "UUID")
        private UUID value;

        public static AuctionId of(UUID id) {
            return new AuctionId(id);
        }

        public static AuctionId generate() {
            return new AuctionId(UUID.randomUUID());
        }

    }
}
