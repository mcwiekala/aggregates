package io.cwiekala.aggregates;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.cwiekala.aggregates.application.AuctionFacade;
import io.cwiekala.aggregates.application.command.CreateAuctionCommand;
import io.cwiekala.aggregates.commands.Result;
import io.cwiekala.aggregates.domain.auction.Auction;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.AuctionCreated;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidWasPlaced;
import io.cwiekala.aggregates.domain.auction.AuctionRepository;
import io.cwiekala.aggregates.infrastructure.InMemoryAuctionRepository;
import io.cwiekala.aggregates.infrastructure.InMemoryEventPublisher;
import io.cwiekala.aggregates.utils.aggregateid.ListingId;
import io.cwiekala.aggregates.utils.aggregateid.AuctioneerId;
import io.cwiekala.aggregates.utils.aggregateid.SellerId;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import lombok.ToString;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

class AuctionTest {

    CurrencyUnit USD = Monetary.getCurrency("USD");
    Money $10 = Money.of(10, USD);
    Money $100 = Money.of(100, USD);


    private final AuctionRepository auctionRepository = new InMemoryAuctionRepository();
    private final InMemoryEventPublisher eventPublisher = new InMemoryEventPublisher();
    AuctionFacade auctionFacade = new AuctionFacade(auctionRepository, eventPublisher);

    @Test
    void bidAboveCurrentPriceWasPlaced() {
        // given
        Duration sevenDays = Duration.of(7, ChronoUnit.DAYS);
        SellerId sellerId = SellerId.generate();
        AuctioneerId auctioneerId = AuctioneerId.generate();
        ListingId listingId = ListingId.generate();

        // when:
        CreateAuctionCommand createAuction = new CreateAuctionCommand(sevenDays, sellerId, listingId, $10);
        Result createAuctionResult = auctionFacade.createAuction(createAuction);

        BidWasPlaced bidWasPlaced = BidWasPlaced.now(createAuction.getAuctionId(), auctioneerId, $100);
        Result handleBidWasPlacedResult = auctionFacade.handle(bidWasPlaced);

        // then:
        assertThat(createAuctionResult).isEqualTo(Result.Success);
        assertThat(handleBidWasPlacedResult).isEqualTo(Result.Success);

        Auction resultAuction = auctionRepository.findById(createAuction.getAuctionId()).get();
        assertThat(resultAuction.getActualPrice()).isEqualTo($10);
        assertThat(resultAuction.getMaximumPrice()).isEqualTo($100);

        List<Object> events = eventPublisher.getEvents();
        assertThat(events.size()).isEqualTo(2);

        EventChecker eventChecker = new EventChecker(events);
        eventChecker.assertEvent(AuctionCreated.class);
        eventChecker.assertEvent(BidWasPlaced.class);
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

    @ToString
    static class EventChecker {
        private List<Object> events;

        EventChecker(List<Object> events) {
            this.events = events;
        }

        private void assertEvent(Class clazz) {
            Optional<Object> foundEvent = events.stream().filter(event -> event.getClass().equals(clazz)).findAny();
            assertThat(foundEvent).isPresent();
        }
    }
}
