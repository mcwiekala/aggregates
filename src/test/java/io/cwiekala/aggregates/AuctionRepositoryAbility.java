package io.cwiekala.aggregates;

import static io.cwiekala.aggregates.AuctionFixture.$10;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.cwiekala.aggregates.domain.auction.Auction;
import io.cwiekala.aggregates.domain.auction.Auction.AuctionId;
import io.cwiekala.aggregates.domain.auction.AuctionFactory;
import io.cwiekala.aggregates.domain.auction.AuctionRepository;
import io.cwiekala.aggregates.infrastructure.InMemoryAuctionRepository;
import io.cwiekala.aggregates.utils.aggregateid.ListingId;
import io.cwiekala.aggregates.utils.aggregateid.SellerId;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

interface AuctionRepositoryAbility {

    AuctionRepository auctionRepository = new InMemoryAuctionRepository();
    AuctionFactory auctionFactory = new AuctionFactory();

    default AuctionId thereIsAnAuctionWith10$StartingPrice() {
        Duration sevenDays = Duration.of(7, ChronoUnit.DAYS);
        SellerId sellerId = SellerId.generate();
        ListingId listingId = ListingId.generate();

        Auction auction = auctionFactory.createAuction(AuctionId.generate(), sevenDays, sellerId, listingId, $10);
        auctionRepository.save(auction);
        return auction.getId();
    }

}
