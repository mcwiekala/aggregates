package io.cwiekala.aggregates;

import io.cwiekala.aggregates.domain.auction.Auction;
import io.cwiekala.aggregates.utils.aggregateid.ListingId;
import io.cwiekala.aggregates.utils.aggregateid.MemberId;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

class AuctionTest {


    @Test
    void bidAboveCurrentPriceWasPlaced() {
        Duration sevenDays = Duration.of(7, ChronoUnit.DAYS);
        MemberId sellerId = MemberId.generate();
        ListingId listingId = ListingId.generate();
        Auction auction = new Auction(sevenDays, sellerId, listingId);


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
