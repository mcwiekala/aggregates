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

    AuctionPolicy leadingAuctioneerCannotDecreaseTheBid = (Auction auction, BidWasPlaced cmd) -> {
        if (auction.getPossibleWinningBid().isEmpty()) {
            return Either.right(new Allowance());
        }
        WinningBid winningBid = auction.getPossibleWinningBid().get();
        if (winningBid.getAuctioneerId().equals(cmd.getAuctioneerId())
            && winningBid.getMaximumPrice().compareTo(cmd.getNewPrice()) >= 0) {
            return Either.left(Rejection.withReason("This user has already bid with higher or equal maximum amount"));
        }
        return Either.right(new Allowance());
    };

    AuctionPolicy newBidMustBeGreaterThanActual = (Auction auction, BidWasPlaced cmd) -> {
        if (auction.getPossibleWinningBid().isEmpty()) {
            return Either.right(new Allowance());
        }
        WinningBid winningBid = auction.getPossibleWinningBid().get();
        if (cmd.getNewPrice().compareTo(winningBid.getActualPrice()) <= 0) {
            return Either.left(Rejection.withReason("Cannot place a bid with lower amount than the actual price"));
        }

        return Either.right(new Allowance());
    };

    static List<AuctionPolicy> standardAuctionPolicies() {
        return List.of(bidCanBePlacedWhenAuctionIsActivePolicy,
            onlyBidAboveAuctionMinimalPriceCanWonPolicy,
            firstOfferMustBeAboveStartingPricePolicy,
            leadingAuctioneerCannotDecreaseTheBid,
            newBidMustBeGreaterThanActual
        );
    }
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