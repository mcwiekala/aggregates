package io.cwiekala.aggregates;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.cwiekala.aggregates.application.AuctionFacade;
import io.cwiekala.aggregates.application.command.CreateAuctionCommand;
import io.cwiekala.aggregates.commands.Result;
import io.cwiekala.aggregates.domain.auction.Auction;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidWasPlaced;
import io.cwiekala.aggregates.domain.auction.AuctionRepository;
import io.cwiekala.aggregates.infrastructure.InMemoryAuctionRepository;
import io.cwiekala.aggregates.utils.aggregateid.ListingId;
import io.cwiekala.aggregates.utils.aggregateid.AuctioneerId;
import io.cwiekala.aggregates.utils.aggregateid.SellerId;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

class AuctionTest {

    CurrencyUnit USD = Monetary.getCurrency("USD");
    private final AuctionRepository auctionRepository = new InMemoryAuctionRepository();
    AuctionFacade auctionFacade = new AuctionFacade(auctionRepository);

    @Test
    void bidAboveCurrentPriceWasPlaced() {
        // given
        Duration sevenDays = Duration.of(7, ChronoUnit.DAYS);
        SellerId sellerId = SellerId.generate();
        AuctioneerId auctioneerId = AuctioneerId.generate();
        ListingId listingId = ListingId.generate();

//        Auction auction = new Auction(sevenDays, sellerId, listingId, Money.of(10, USD));
        CreateAuctionCommand createAuction = new CreateAuctionCommand(sevenDays, sellerId, listingId,
            Money.of(10, USD));

        Money $100 = Money.of(100, USD);
        BidWasPlaced bidWasPlaced = BidWasPlaced.now(createAuction.getAuctionId(), auctioneerId, $100);

        // when:
        Result createAuctionResult = auctionFacade.createAuction(createAuction);
        Result handleBidWasPlacedResult = auctionFacade.handle(bidWasPlaced);

        // then:
        assertThat(createAuctionResult).isEqualTo(Result.Success);
        assertThat(handleBidWasPlacedResult).isEqualTo(Result.Success);
        
        Auction resultAuction = auctionRepository.findById(createAuction.getAuctionId()).get();
        Money currentPrice = resultAuction.getActualPrice();
        assertThat(currentPrice).isEqualTo($100);
    }

    @Test
    void bidAboveMaxBidWasPlaced() {
        // winningBidWasChanged
    }

    @Test
    void bidBelowStartingPriceWasPlaced() {

    }

    @Test
    void auctionerWithWinningBidIncreasesMaxBid() {

    }

    @Test
    void auctionerWithWinningBidCannotDecreaseCurrentPrice() {

    }

    @Test
    void bidWasPlacedAfterAuctionEnd() {

    }
}
