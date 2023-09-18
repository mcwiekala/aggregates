package io.cwiekala.aggregates.application;

import static io.cwiekala.aggregates.commands.Result.Success;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;

import io.cwiekala.aggregates.commands.Result;
import io.cwiekala.aggregates.domain.auction.Auction;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidWasPlaced;
import io.cwiekala.aggregates.domain.auction.AuctionRepository;
import io.cwiekala.aggregates.application.command.CreateAuctionCommand;
import io.cwiekala.aggregates.domain.bid.BidPlacementFailure;
import io.cwiekala.aggregates.domain.bid.BidWasPlacedOLD;
import io.cwiekala.aggregates.utils.ApplicationService;
import io.vavr.control.Either;
import java.util.Optional;
import lombok.AllArgsConstructor;

@ApplicationService
@AllArgsConstructor
public class AuctionFacade {

    private AuctionRepository auctionRepository;

    public Result createAuction(CreateAuctionCommand command) {
        Auction newAuction = new Auction(command.getAuctionLength(), command.getSellerId(), command.getListingId(), command.getStartingPrice());
        auctionRepository.save(newAuction);
        return Success;
    }

    public Result handle(BidWasPlaced event) {
        Optional<Auction> possibleAuction = auctionRepository.findById(event.auctionId());
        if (possibleAuction.isEmpty()) {
            Auction auction = possibleAuction.get();
            Either<BidPlacementFailure, BidWasPlacedOLD> result = auction.handle(event);

            return Match(result).of(
                Case($Left($()), this::publishEvents),
                Case($Right($()), this::publishEvents)
            );
        }

        return Result.Rejection;
//        return announceFailure(BidPlaceFailed.now(event.getAggregateId()));
    }

    private Result publishEvents(BidPlacementFailure placedOnHold) {
//        patronRepository.publish(placedOnHold);
        return Success;
    }

    private Result publishEvents(BidWasPlacedOLD bookHoldFailed) {
//        patronRepository.publish(bookHoldFailed);
        return Result.Rejection;
    }


}
