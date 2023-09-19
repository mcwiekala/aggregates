package io.cwiekala.aggregates;

import static io.cwiekala.aggregates.AuctionFixture.$10;
import static io.cwiekala.aggregates.AuctionFixture.$100;

import io.cwiekala.aggregates.application.AuctionFacade;
import io.cwiekala.aggregates.application.command.CreateAuctionCommand;
import io.cwiekala.aggregates.domain.auction.Auction.AuctionId;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidWasPlaced;
import io.cwiekala.aggregates.utils.aggregateid.AuctioneerId;
import io.cwiekala.aggregates.utils.aggregateid.ListingId;
import io.cwiekala.aggregates.utils.aggregateid.SellerId;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public interface AuctionFacadeAbility {

    AuctionFacade auctionFacade = new AuctionFacade(AuctionRepositoryAbility.auctionRepository, EventsAbility.eventPublisher);

    default AuctionId thereIsAnAuctionWith100$Bid() {
        Duration sevenDays = Duration.of(7, ChronoUnit.DAYS);
        SellerId sellerId = SellerId.generate();
        AuctioneerId auctioneerId = AuctioneerId.generate();
        ListingId listingId = ListingId.generate();

        CreateAuctionCommand createAuction = new CreateAuctionCommand(sevenDays, sellerId, listingId, $10);
        auctionFacade.createAuction(createAuction);

        BidWasPlaced bidWasPlaced = BidWasPlaced.now(createAuction.getAuctionId(), auctioneerId, $100);
        auctionFacade.handle(bidWasPlaced);
        return createAuction.getAuctionId();
    }

}
