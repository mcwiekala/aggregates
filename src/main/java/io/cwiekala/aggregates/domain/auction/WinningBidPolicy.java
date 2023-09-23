package io.cwiekala.aggregates.domain.auction;

import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidWasPlaced;
import io.vavr.Function2;
import io.vavr.control.Either;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.Value;
import org.javamoney.moneta.Money;


public interface WinningBidPolicy extends
    Function2<WinningBid, BidWasPlaced, Either<Rejection, Allowance>> {

    // Bid i WinningBid? Komenda?
    WinningBidPolicy actualLeadingAuctioneerCanIncreaseBid = (WinningBid winningBid, BidWasPlaced cmd) -> {
        if (cmd.getAuctionId().equals(winningBid.getAuctioneerId())) {
            if (cmd.getNewPrice().compareTo(winningBid.getMaximumPrice()) > 0) {
                return Either.right(new Allowance()); // result update actual
            }
            return Either.left(Rejection.withReason("Actual leading auctioneer can't decrease the maximum amount"));
        }
        return Either.right(new Allowance()); // results with different Event!! BidUpdated?
    };

    WinningBidPolicy newBidMustBeGreaterThanMaximumPrice = (WinningBid winningBid, BidWasPlaced cmd) -> {
        if (cmd.getNewPrice().compareTo(winningBid.getMaximumPrice()) > 0) {
            return Either.right(new Allowance());
        }
        return Either.left(Rejection.withReason("New bid is not greater than the maximum price!")); // result update actual
    };

    static List<WinningBidPolicy> standardWinningBidPolicies() {
        return List.of(actualLeadingAuctioneerCanIncreaseBid,
            newBidMustBeGreaterThanMaximumPrice);
    }

//    Either<BidPlacementFailure, AuctionEvent> processNewOffer(BidWasPlaced event) {
//        Money newPrice = event.getNewPrice();
//
//        if (isGreaterThanMaximumPrice(newPrice)) {
//            if (hasAuctionerAlreadyAWinningBid(event)) {
//                maximumPrice = Money.from(event.getNewPrice());
//                return announceSuccess(
//                    WinningBidWasUpdated.now(event.getAuctionId(), event.getAuctioneerId(), event.getNewPrice()));
//            } else {
//                auctioneerId = event.getAuctioneerId();
//                actualPrice = Money.from(maximumPrice);
//                maximumPrice = Money.from(event.getNewPrice());
//                return announceSuccess(WinningBidWasChangedWithNewOne.now(event.getAuctionId(), event.getAuctioneerId(),
//                    event.getNewPrice()));
//            }
//        } else if (hasAuctionerAlreadyAWinningBid(event)) {
//            return announceFailure(BidPlacementFailure.now(event.getAuctionId(), event.getAuctioneerId(), "This used has already bid with higher maximum amount"));
//        } else if (isGreaterThanActualPrice(newPrice)) {
//            actualPrice = Money.from(newPrice);
//            return announceSuccess(
//                WinningBidWasUpdated.now(event.getAuctionId(), event.getAuctioneerId(), event.getNewPrice()));
//        }
//        return announceFailure(BidPlacementFailure.now(event.getAuctionId(), event.getAuctioneerId(), "Bid offer was too low!"));
//    }

//    private boolean isNewOfferGreaterThanStartingPrice(BidWasPlaced event) {
//        return event.getNewPrice().compareTo(startingPrice) > 0;
//    }
//
//    private boolean doesEventHappenWhenAuctionIsInactive(LocalDateTime eventTime) {
//        return !eventTime.isAfter(startDate)
//            || !eventTime.isBefore(endDate);
//    }

//    public Either<BidPlacementFailure, AuctionEvent> handle(BidWasPlaced event) {
//        // check end date
//        LocalDateTime eventTime = event.getEventTime();
//        if (doesEventHappenWhenAuctionIsInactive(eventTime)) {
//            return announceFailure(
//                BidPlacementFailure.now(event.getAuctionId(), event.getAuctioneerId(),
//                    "Bid was placed when the auction was inactive"));
//        }
//        if (minimalSellingPrice.isPresent()
//            && event.getNewPrice().compareTo(minimalSellingPrice.get()) > 0) {
//
//        }
//        if (possibleWinningBid.isEmpty()) {
//            if (isNewOfferGreaterThanStartingPrice(event)) {
//                WinningBid winningBid = new WinningBid(startingPrice, event.getNewPrice(), event.getAuctioneerId(),
//                    event.getEventTime());
//                possibleWinningBid = Optional.of(winningBid);
//                return announceSuccess(WinningBidWasChangedWithNewOne.now(event.getAuctionId(), event.getAuctioneerId(),
//                    event.getNewPrice()));
//            }
//            return announceFailure(BidPlacementFailure.now(event.getAuctionId(), event.getAuctioneerId(),
//                "Bid offer is lower than starting price"));
//        } else {
//            return possibleWinningBid.get().processNewOffer(event);
//        }
//    }
//
//    private boolean isNewOfferGreaterThanStartingPrice(BidWasPlaced event) {
//        return event.getNewPrice().compareTo(startingPrice) > 0;
//    }
//
//    private boolean doesEventHappenWhenAuctionIsInactive(LocalDateTime eventTime) {
//        return !eventTime.isAfter(startDate)
//            || !eventTime.isBefore(endDate);
//    }
//
//    public void handle(UpdateAuction command) {
//        // what can be updated?
//        // shipment type and payment can be only added
//    }
//
//    public Money getActualPrice() {
//        return possibleWinningBid.map(WinningBid::getActualPrice)
//            .orElse(startingPrice);
//    }
//
//    public Money getMaximumPrice() {
//        return possibleWinningBid.map(WinningBid::getMaximumPrice)
//            .orElse(startingPrice);
//    }
//
//    public Optional<AuctioneerId> getWinningAuctioneerId() {
//        return possibleWinningBid.map(WinningBid::getAuctioneerId);
//    }
}