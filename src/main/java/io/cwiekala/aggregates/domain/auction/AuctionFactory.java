package io.cwiekala.aggregates.domain.auction;

import static io.cwiekala.aggregates.domain.auction.AuctionPolicy.standardAuctionPolicies;
import static io.cwiekala.aggregates.domain.bid.Bid.USD;

import io.cwiekala.aggregates.domain.auction.Auction.AuctionId;
import io.cwiekala.aggregates.utils.aggregateid.ListingId;
import io.cwiekala.aggregates.utils.aggregateid.SellerId;
import io.cwiekala.aggregates.utils.comments.AuctionAggregate;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import lombok.ToString;
import org.javamoney.moneta.Money;

@AuctionAggregate
@Getter
@ToString
public class AuctionFactory {

    public Auction createAuction(AuctionId id, Duration auctionLength, SellerId sellerId, ListingId listingId, Money startingPrice) {
        return new Auction(id, auctionLength, sellerId, listingId, startingPrice, standardAuctionPolicies());
    }

    public Auction createDefaultAuction(AuctionId id, SellerId sellerId, ListingId listingId) {
        return new Auction(id, Duration.of(7, ChronoUnit.DAYS), sellerId, listingId, Money.of(0, USD), standardAuctionPolicies());
    }

}
