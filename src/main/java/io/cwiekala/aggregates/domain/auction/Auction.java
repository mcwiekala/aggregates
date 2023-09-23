package io.cwiekala.aggregates.domain.auction;

import static io.cwiekala.aggregates.commons.events.EitherResult.announceFailure;
import static io.cwiekala.aggregates.commons.events.EitherResult.announceSuccess;

import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidPlacementFailure;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidWasPlaced;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.WinningBidWasChangedWithNewOne;
import io.cwiekala.aggregates.utils.aggregateid.AuctioneerId;
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
import java.util.List;
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
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ListingId listingId;
    private SellerId sellerId;
    private Money startingPrice;
    private Optional<WinningBid> possibleWinningBid = Optional.empty();
    private List<AuctionPolicy> auctionPolicies;
    private Optional<Money> possibleMinimalSellingPrice = Optional.empty(); // TODO??? AuctionConfiguration? // policy

    Auction(AuctionId id, Duration auctionLength, SellerId sellerId, ListingId listingId, Money startingPrice,
        List<AuctionPolicy> auctionPolicies) {
        this.id = id;
        LocalDateTime now = LocalDateTime.now();
        this.startDate = now; // TODO: as params?
        this.endDate = now.plus(auctionLength);
        this.listingId = listingId;
        this.sellerId = sellerId;
        this.startingPrice = startingPrice;
        this.auctionPolicies = auctionPolicies;
    }

    public Either<BidPlacementFailure, AuctionEvent> handle(BidWasPlaced event) {
        Optional<Rejection> rejection = canBidBePlaced(event);
        if (rejection.isPresent()) {
            return announceFailure(BidPlacementFailure.now(event.getAuctionId(), event.getAuctioneerId(),
                rejection.get().getReason().getMessage()));
        }
        if (possibleWinningBid.isPresent()) {
            return possibleWinningBid.get().processNewOffer(event);
        } else {
            WinningBid winningBid = new WinningBid(startingPrice, event.getNewPrice(), event.getAuctioneerId(),
                event.getEventTime());
            possibleWinningBid = Optional.of(winningBid);
            return announceSuccess(WinningBidWasChangedWithNewOne.now(event.getAuctionId(), event.getAuctioneerId(),
                event.getNewPrice()));
        }
    }

    private Optional<Rejection> canBidBePlaced(BidWasPlaced event) {
        return auctionPolicies.stream()
            .map(policy -> policy.apply(this, event))
            .filter(Either::isLeft)
            .findAny()
            .map(Either::getLeft);
    }

    private boolean isNewOfferGreaterThanStartingPrice(BidWasPlaced event) {
        return event.getNewPrice().compareTo(startingPrice) > 0;
    }

    private boolean doesEventHappenWhenAuctionIsInactive(LocalDateTime eventTime) {
        return !eventTime.isAfter(startDate)
            || !eventTime.isBefore(endDate);
    }

    public Money getActualPrice() {
        return possibleWinningBid.map(WinningBid::getActualPrice)
            .orElse(startingPrice);
    }

    public Money getMaximumPrice() {
        return possibleWinningBid.map(WinningBid::getMaximumPrice)
            .orElse(startingPrice);
    }

    public Optional<AuctioneerId> getWinningAuctioneerId() {
        return possibleWinningBid.map(WinningBid::getAuctioneerId);
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
