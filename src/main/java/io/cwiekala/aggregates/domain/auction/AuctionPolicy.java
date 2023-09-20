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


public interface AuctionPolicy extends
    Function2<Auction, BidWasPlaced, Either<Rejection, Allowance>> {

    // Bid i WinningBid? Komenda?
    AuctionPolicy bidCanBePlacedWhenAuctionIsActivePolicy = (Auction auction, BidWasPlaced bidWasPlaced) -> {
        LocalDateTime bidTime = bidWasPlaced.getEventTime();
        if (bidTime.isAfter(auction.getStartDate()) && bidTime.isBefore(auction.getEndDate())) {
            return Either.right(new Allowance());
        }
        return Either.left(Rejection.withReason("Bid cannot be placed when the auction is inactive!"));
    };

    AuctionPolicy onlyBidAboveAuctionMinimalPriceCanWonPolicy = (Auction auction, BidWasPlaced bidWasPlaced) -> {
        Optional<Money> possibleMinimalSellingPrice = auction.getPossibleMinimalSellingPrice();
        if (possibleMinimalSellingPrice.isPresent()) {
            if (bidWasPlaced.getNewPrice().compareTo(possibleMinimalSellingPrice.get()) > 1) {
                return Either.right(new Allowance());
            } else {
                return Either.left(
                    Rejection.withReason("Auction will not be won if the bid is not higher than the minimal price!"));
            }
        } else {
            return Either.right(new Allowance());

        }
    };

    AuctionPolicy firstOfferMustBeAboveStartingPricePolicy = (Auction auction, BidWasPlaced bidWasPlaced) -> {
        if (auction.getPossibleWinningBid().isEmpty()) {
            if (bidWasPlaced.getNewPrice().compareTo(auction.getStartingPrice()) > 0) {
                return Either.right(new Allowance());
            }
            return Either.left(Rejection.withReason("Bid below starting price can not be placed!"));
        }
        return Either.right(new Allowance());
    };

    static List<AuctionPolicy> standardAuctionPolicies() {
        return List.of(bidCanBePlacedWhenAuctionIsActivePolicy,
            onlyBidAboveAuctionMinimalPriceCanWonPolicy,
            firstOfferMustBeAboveStartingPricePolicy);
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

@Value
class Allowance {

}

@Value
class Rejection {

    @Value
    static class Reason {

        @NonNull
        String message;
    }

    @NonNull
    Reason reason;

    static Rejection withReason(String message) {
        return new Rejection(new Reason(message));
    }
}