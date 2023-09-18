package io.cwiekala.aggregates.domain.auction;

import static io.cwiekala.aggregates.commons.events.EitherResult.announceFailure;
import static io.cwiekala.aggregates.commons.events.EitherResult.announceSuccess;

import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidPlacementFailure;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidWasPlaced;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.WinningBidWasChangedWithNewOne;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.WinningBidWasUpdated;
import io.cwiekala.aggregates.utils.Entity;
import io.cwiekala.aggregates.utils.aggregateid.AuctioneerId;
import io.vavr.control.Either;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.javamoney.moneta.Money;

@Entity
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
        return actualPrice.compareTo(newPrice) > 0;
    }

    boolean isGreaterThanMaximumPrice(Money newPrice) {
        return maximumPrice.compareTo(newPrice) > 0;
    }

    Either<BidPlacementFailure, AuctionEvent> processNewOffer(BidWasPlaced event) {
        Money newPrice = event.getNewPrice();

        if (isGreaterThanMaximumPrice(newPrice)) {
            if (hasAuctionerAlreadyAWinningBid(event)) {
                maximumPrice = event.getNewPrice();
                return announceSuccess(
                    WinningBidWasUpdated.now(event.getAuctionId(), event.getAuctioneerId(), event.getNewPrice()));
            } else {
                auctioneerId = event.getAuctioneerId();
                actualPrice = maximumPrice;
                return announceSuccess(WinningBidWasChangedWithNewOne.now(event.getAuctionId(), event.getAuctioneerId(),
                    event.getNewPrice()));
            }
        } else if (isGreaterThanActualPrice(newPrice)) {
            actualPrice = newPrice;
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
}
