package io.cwiekala.aggregates.domain.auction;

import static io.cwiekala.aggregates.commons.events.EitherResult.announceFailure;
import static io.cwiekala.aggregates.commons.events.EitherResult.announceSuccess;

import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidPlacementFailure;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidWasPlaced;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.WinningBidWasChangedWithNewOne;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.WinningBidWasUpdated;
import io.cwiekala.aggregates.utils.aggregateid.AuctioneerId;
import io.cwiekala.aggregates.utils.comments.AuctionAggregate;
import io.cwiekala.aggregates.utils.comments.Entity;
import io.vavr.control.Either;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;
import org.javamoney.moneta.Money;

@Entity
@AuctionAggregate
@Getter
@ToString
class WinningBid {

    private UUID id;

    private Money actualPrice;

    private Money maximumPrice;
    private AuctioneerId auctioneerId;
    private LocalDateTime bidDate;
    WinningBid(Money actualPrice, Money maximumPrice, AuctioneerId auctioneerId, LocalDateTime bidDate) {
        this.id = UUID.randomUUID();
        this.actualPrice = actualPrice;
        this.maximumPrice = maximumPrice;
        this.auctioneerId = auctioneerId;
        this.bidDate = bidDate;
    }

    boolean isGreaterThanActualPrice(Money newPrice) {
        return newPrice.compareTo(actualPrice) > 0;
    }

    boolean isGreaterThanMaximumPrice(Money newPrice) {
        return newPrice.compareTo(maximumPrice) > 0;
    }

    Either<BidPlacementFailure, AuctionEvent> processNewOffer(BidWasPlaced event) {
        Money newPrice = event.getNewPrice();

        if (isGreaterThanMaximumPrice(newPrice)) {
            if (hasAuctionerAlreadyAWinningBid(event)) {
                maximumPrice = Money.from(event.getNewPrice());
                return announceSuccess(
                    WinningBidWasUpdated.now(event.getAuctionId(), event.getAuctioneerId(), event.getNewPrice()));
            } else {
                auctioneerId = event.getAuctioneerId();
                actualPrice = Money.from(maximumPrice);
                maximumPrice = Money.from(event.getNewPrice());
                return announceSuccess(WinningBidWasChangedWithNewOne.now(event.getAuctionId(), event.getAuctioneerId(),
                    event.getNewPrice()));
            }
        } else if (hasAuctionerAlreadyAWinningBid(event)) {
            return announceFailure(BidPlacementFailure.now(event.getAuctionId(), event.getAuctioneerId(), "This used has already bid with higher maximum amount"));
        } else if (isGreaterThanActualPrice(newPrice)) {
            actualPrice = Money.from(newPrice);
            return announceSuccess(
                WinningBidWasUpdated.now(event.getAuctionId(), event.getAuctioneerId(), event.getNewPrice()));
        }
        return announceFailure(BidPlacementFailure.now(event.getAuctionId(), event.getAuctioneerId(), "Bid offer was too low!"));
    }

    private boolean hasAuctionerAlreadyAWinningBid(BidWasPlaced event) {
        return event.getAuctioneerId().equals(auctioneerId);
    }

    Money getActualPrice() {
        return actualPrice;
    }

    Money getMaximumPrice() {
        return maximumPrice;
    }
}
