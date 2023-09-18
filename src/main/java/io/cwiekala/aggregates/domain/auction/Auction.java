package io.cwiekala.aggregates.domain.auction;

import static io.cwiekala.aggregates.commons.events.EitherResult.announceFailure;
import static io.cwiekala.aggregates.commons.events.EitherResult.announceSuccess;

import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidPlacementFailure;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidWasPlaced;
import io.cwiekala.aggregates.application.command.CreateAuctionCommand;
import io.cwiekala.aggregates.application.command.UpdateAuction;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.WinningBidWasChangedWithNewOne;
import io.cwiekala.aggregates.utils.comments.AggregateRoot;
import io.cwiekala.aggregates.utils.aggregateid.ListingId;
import io.cwiekala.aggregates.utils.aggregateid.SellerId;
import io.cwiekala.aggregates.utils.comments.AuctionAggregate;
import io.vavr.control.Either;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import java.time.Duration;
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
@AuctionAggregate
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
    Optional<WinningBid> possibleWinningBid = Optional.empty();
    Optional<Money> minimalSellingPrice = Optional.empty(); // TODO??? AuctionConfiguration?

    public Auction(AuctionId id, Duration auctionLength, SellerId sellerId, ListingId listingId, Money startingPrice) {
        this.id = id;
        LocalDateTime now = LocalDateTime.now();
        this.startDate = now; // TODO: as params?
        this.endDate = now.plus(auctionLength);
        this.listingId = listingId;
        this.sellerId = sellerId;
        this.startingPrice = startingPrice;
    }

    // questions and answers?

    public void create(CreateAuctionCommand command) {
        //
    }

    public Either<BidPlacementFailure, AuctionEvent> handle(BidWasPlaced event) {
        // check end date
        LocalDateTime eventTime = event.getEventTime();
        if (doesEventHappenWhenAuctionIsInactive(eventTime)) { // TODO: atomic invariant
            return announceFailure(
                BidPlacementFailure.now(event.getAuctionId(), event.getAuctioneerId(),
                    "Bid was placed when the auction was inactive"));
        }
        if (minimalSellingPrice.isPresent()
            && event.getNewPrice().compareTo(minimalSellingPrice.get()) > 0) {

        }
        if (possibleWinningBid.isEmpty()) {
            if (isNewOfferGreaterThanStartingPrice(event)) {
                WinningBid winningBid = new WinningBid(startingPrice, event.getNewPrice(), event.getAuctioneerId(),
                    event.getEventTime());
                possibleWinningBid = Optional.of(winningBid);
                return announceSuccess(WinningBidWasChangedWithNewOne.now(event.getAuctionId(), event.getAuctioneerId(),
                    event.getNewPrice()));
            }
            return announceFailure(BidPlacementFailure.now(event.getAuctionId(), event.getAuctioneerId(),
                "Bid offer is lower than starting price"));
        } else {
            return possibleWinningBid.get().processNewOffer(event);
        }
//        return possibleWinningBid.processNewOffer(event);
    }

    private boolean isNewOfferGreaterThanStartingPrice(BidWasPlaced event) {
        return event.getNewPrice().compareTo(startingPrice) > 0;
    }

    private boolean doesEventHappenWhenAuctionIsInactive(LocalDateTime eventTime) {
        return !eventTime.isAfter(startDate)
            || !eventTime.isBefore(endDate);
    }

    public void handle(UpdateAuction command) {
        // what can be updated?
        // shipment type and payment can be only added
    }

    public Money getActualPrice() {
        return possibleWinningBid.map(WinningBid::getActualPrice)
            .orElse(startingPrice);
    }

    public Money getMaximumPrice() {
        return possibleWinningBid.map(WinningBid::getMaximumPrice)
            .orElse(startingPrice);
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
