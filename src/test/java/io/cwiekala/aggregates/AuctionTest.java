package io.cwiekala.aggregates;

import static io.cwiekala.aggregates.AuctionFixture.$10;
import static io.cwiekala.aggregates.AuctionFixture.$100;
import static io.cwiekala.aggregates.AuctionFixture.$120;
import static io.cwiekala.aggregates.AuctionFixture.$5;
import static io.cwiekala.aggregates.AuctionFixture.$80;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.cwiekala.aggregates.application.command.CreateAuctionCommand;
import io.cwiekala.aggregates.commons.commands.Result;
import io.cwiekala.aggregates.domain.auction.Auction;
import io.cwiekala.aggregates.domain.auction.Auction.AuctionId;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.AuctionCreated;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidPlacementFailure;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidWasPlaced;
import io.cwiekala.aggregates.utils.aggregateid.ListingId;
import io.cwiekala.aggregates.utils.aggregateid.AuctioneerId;
import io.cwiekala.aggregates.utils.aggregateid.SellerId;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;

class AuctionTest implements AuctionFacadeAbility, EventsAbility, AuctionRepositoryAbility {

    @Test
    void bidAboveCurrentPriceWasPlaced() {
        // given:
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
        eventChecker.assertEventAndPop(AuctionCreated.class);
        eventChecker.assertEventAndPop(BidWasPlaced.class);
    }

    @Test
    void anotherBidWasPlaced() {
        // given:
        AuctionId auctionId = thereIsAnAuctionWith100$Bid();

        // when:
        AuctioneerId auctioneerTomId = AuctioneerId.generate();
        BidWasPlaced tomsBidWasPlaced = BidWasPlaced.now(auctionId, auctioneerTomId, $120);
        Result handleBidWasPlacedResult = auctionFacade.handle(tomsBidWasPlaced);

        // then:
        assertThat(handleBidWasPlacedResult).isEqualTo(Result.Success);

        Auction resultAuction = auctionRepository.findById(auctionId).orElseThrow();
        assertThat(resultAuction.getActualPrice()).isEqualTo($100);
        assertThat(resultAuction.getMaximumPrice()).isEqualTo($120);
        assertThat(resultAuction.getWinningAuctioneerId().orElseThrow()).isEqualTo(auctioneerTomId);

        List<Object> events = eventPublisher.getEvents();
        assertThat(events.size()).isEqualTo(3);
        EventChecker eventChecker = new EventChecker(events);
        eventChecker.assertEventAndPop(AuctionCreated.class);
        eventChecker.assertEventAndPop(BidWasPlaced.class);
        eventChecker.assertEventAndPop(BidWasPlaced.class);
    }

    @Test
    void newBidWasPlaced() {
        // given:
        AuctionId auctionId = thereIsAnAuctionWith100$Bid();

        // when:
        AuctioneerId auctioneerTomId = AuctioneerId.generate();
        BidWasPlaced tomsBidWasPlaced = BidWasPlaced.now(auctionId, auctioneerTomId, $80);
        Result handleBidWasPlacedResult = auctionFacade.handle(tomsBidWasPlaced);

        // then:
        assertThat(handleBidWasPlacedResult).isEqualTo(Result.Success);

        Auction resultAuction = auctionRepository.findById(auctionId).orElseThrow();
        assertThat(resultAuction.getActualPrice()).isEqualTo($80);
        assertThat(resultAuction.getMaximumPrice()).isEqualTo($100);
        assertThat(resultAuction.getWinningAuctioneerId().orElseThrow()).isNotEqualTo(auctioneerTomId);

        assertThatBidEventsHappened();
    }

    @Test
    void bidBelowStartingPriceWasPlaced() {
        // given:
        AuctionId auctionId = thereIsAnAuctionWith10$StartingPrice();

        // when:
        AuctioneerId auctioneerTomId = AuctioneerId.generate();
        BidWasPlaced tomsBidWasPlaced = BidWasPlaced.now(auctionId, auctioneerTomId, $5);
        Result handleBidWasPlacedResult = auctionFacade.handle(tomsBidWasPlaced);

        // then:
        assertThat(handleBidWasPlacedResult).isEqualTo(Result.Rejection);

        Auction resultAuction = auctionRepository.findById(auctionId).orElseThrow();
        assertThat(resultAuction.getActualPrice()).isEqualTo($10);
        assertThat(resultAuction.getMaximumPrice()).isEqualTo($10);
        assertThat(resultAuction.getWinningAuctioneerId()).isEmpty();

        List<Object> events = eventPublisher.getEvents();
        assertThat(events.size()).isEqualTo(1);
        EventChecker eventChecker = new EventChecker(events);
        eventChecker.assertEventAndPop(BidPlacementFailure.class);
    }

    @Test
    void auctionerWithWinningBidIncreasesMaxBid() {
        // given:
        AuctionId auctionId = thereIsAnAuctionWith10$StartingPrice();

        // when:
        AuctioneerId auctioneerId = AuctioneerId.generate();
        BidWasPlaced bidWasPlaced = BidWasPlaced.now(auctionId, auctioneerId, $80);
        Result handleBidWasPlacedResult = auctionFacade.handle(bidWasPlaced);

        BidWasPlaced bidWasPlacedWithGreaterAmount = BidWasPlaced.now(auctionId, auctioneerId, $100);
        Result handleBidWasPlacedResultWithGreaterAmount = auctionFacade.handle(bidWasPlacedWithGreaterAmount);

        // then:
        assertThat(handleBidWasPlacedResult).isEqualTo(Result.Success);
        assertThat(handleBidWasPlacedResultWithGreaterAmount).isEqualTo(Result.Success);

        Auction resultAuction = auctionRepository.findById(auctionId).orElseThrow();
        assertThat(resultAuction.getActualPrice()).isEqualTo($10);
        assertThat(resultAuction.getMaximumPrice()).isEqualTo($100);
        assertThat(resultAuction.getWinningAuctioneerId().get()).isEqualTo(auctioneerId);

        List<Object> events = eventPublisher.getEvents();
        assertThat(events.size()).isEqualTo(2);
        EventChecker eventChecker = new EventChecker(events);
        eventChecker.assertEventAndPop(BidWasPlaced.class);
        eventChecker.assertEventAndPop(BidWasPlaced.class);
    }

    @Test
    void auctionerWithWinningBidCannotDecreaseCurrentPrice() {
        // given:
        AuctionId auctionId = thereIsAnAuctionWith10$StartingPrice();

        // when:
        AuctioneerId auctioneerId = AuctioneerId.generate();
        BidWasPlaced bidWasPlaced = BidWasPlaced.now(auctionId, auctioneerId, $100);
        Result handleBidWasPlacedResult = auctionFacade.handle(bidWasPlaced);

        BidWasPlaced bidWasPlacedWithGreaterAmount = BidWasPlaced.now(auctionId, auctioneerId, $80);
        Result handleBidWasPlacedResultWithLesserAmount = auctionFacade.handle(bidWasPlacedWithGreaterAmount);

        // then:
        assertThat(handleBidWasPlacedResult).isEqualTo(Result.Success);
        assertThat(handleBidWasPlacedResultWithLesserAmount).isEqualTo(Result.Rejection);

        Auction resultAuction = auctionRepository.findById(auctionId).orElseThrow();
        assertThat(resultAuction.getActualPrice()).isEqualTo($10);
        assertThat(resultAuction.getMaximumPrice()).isEqualTo($100);
        assertThat(resultAuction.getWinningAuctioneerId().get()).isEqualTo(auctioneerId);

        List<Object> events = eventPublisher.getEvents();
        assertThat(events.size()).isEqualTo(2);
        EventChecker eventChecker = new EventChecker(events);
        eventChecker.assertEventAndPop(BidWasPlaced.class);
        eventChecker.assertEventAndPop(BidPlacementFailure.class);
    }

    @Test
    void bidWasPlacedAfterAuctionEnd() {
        // given:
        AuctionId auctionId = thereIsAnAuctionWith10$StartingPrice();
        Auction createdAuction = auctionRepository.findById(auctionId).orElseThrow();

        // when:
        AuctioneerId auctioneerTom = AuctioneerId.generate();
        BidWasPlaced tomsBidWasPlaced = BidWasPlaced.now(auctionId, auctioneerTom, $80);
        Result handleBidWasPlacedResult = auctionFacade.handle(tomsBidWasPlaced);

        AuctioneerId auctioneerMark = AuctioneerId.generate();
        LocalDateTime timeAfterAuctionEnd = createdAuction.getEndDate().plus(1, ChronoUnit.MINUTES);
        BidWasPlaced marksBidWasPlaced = BidWasPlaced.create(auctionId, auctioneerMark, $10, timeAfterAuctionEnd);
        Result handleMarkBidWasPlacedResult = auctionFacade.handle(marksBidWasPlaced);

        // then:
        assertThat(handleBidWasPlacedResult).isEqualTo(Result.Success);
        assertThat(handleMarkBidWasPlacedResult).isEqualTo(Result.Rejection);

        Auction resultAuction = auctionRepository.findById(auctionId).orElseThrow();
        assertThat(resultAuction.getActualPrice()).isEqualTo($10);
        assertThat(resultAuction.getMaximumPrice()).isEqualTo($80);
        assertThat(resultAuction.getWinningAuctioneerId().get()).isEqualTo(auctioneerTom);

        List<Object> events = eventPublisher.getEvents();
        assertThat(events.size()).isEqualTo(2);
        EventChecker eventChecker = new EventChecker(events);
        eventChecker.assertEventAndPop(BidWasPlaced.class);
        eventChecker.assertEventAndPop(BidPlacementFailure.class);
    }

}
