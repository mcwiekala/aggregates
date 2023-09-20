package io.cwiekala.aggregates.application;

import static io.cwiekala.aggregates.domain.auction.AuctionPolicy.standardAuctionPolicies;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Left;
import static io.vavr.Patterns.$Right;

import io.cwiekala.aggregates.commons.commands.Result;
import io.cwiekala.aggregates.domain.auction.Auction;
import io.cwiekala.aggregates.domain.auction.AuctionEvent;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.AuctionCreated;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidPlacementFailure;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidWasPlaced;
import io.cwiekala.aggregates.domain.auction.AuctionFactory;
import io.cwiekala.aggregates.domain.auction.AuctionRepository;
import io.cwiekala.aggregates.application.command.CreateAuctionCommand;
import io.cwiekala.aggregates.utils.comments.ApplicationService;
import io.vavr.control.Either;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

@ApplicationService
public class AuctionFacade {

    private AuctionRepository auctionRepository;
    private ApplicationEventPublisher eventPublisher;
    private AuctionFactory auctionFactory = new AuctionFactory();

    public AuctionFacade(AuctionRepository auctionRepository, ApplicationEventPublisher eventPublisher) {
        this.auctionRepository = auctionRepository;
        this.eventPublisher = eventPublisher;
    }

    public Result createAuction(CreateAuctionCommand command) {
        Auction newAuction = auctionFactory.createAuction(command.getAuctionId(), command.getAuctionLength(), command.getSellerId(),
            command.getListingId(), command.getStartingPrice());
        auctionRepository.save(newAuction);
        return publishEvents(AuctionCreated.now(command.getAuctionId(), command.getListingId(), command.getSellerId()));
    }

    @EventListener
    public Result handle(BidWasPlaced event) {
        Optional<Auction> possibleAuction = auctionRepository.findById(event.getAuctionId());
        if (possibleAuction.isPresent()) {
            Auction auction = possibleAuction.get();
            Either<BidPlacementFailure, AuctionEvent> result = auction.handle(event);
            auctionRepository.save(auction);

            return Match(result).of(
                Case($Left($()), this::publishEvents),
                Case($Right($()), this::publishEvents)
            );
        }
        return publishEvents(
            BidPlacementFailure.now(event.getAuctionId(), event.getAuctioneerId(), "Given auction not exists"));
    }

    private Result publishEvents(BidPlacementFailure placedOnHold) {
        eventPublisher.publishEvent(placedOnHold);
        return Result.Rejection;
    }

    private Result publishEvents(AuctionEvent auctionEvent) {
        eventPublisher.publishEvent(auctionEvent);
        return Result.Success;
    }

}
