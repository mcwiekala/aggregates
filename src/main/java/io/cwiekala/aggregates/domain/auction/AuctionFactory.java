package io.cwiekala.aggregates.domain.auction;

import static io.cwiekala.aggregates.commons.events.EitherResult.announceFailure;
import static io.cwiekala.aggregates.commons.events.EitherResult.announceSuccess;
import static io.cwiekala.aggregates.domain.auction.AuctionPolicy.standardAuctionPolicies;
import static io.cwiekala.aggregates.domain.bid.Bid.USD;

import io.cwiekala.aggregates.application.command.CreateAuctionCommand;
import io.cwiekala.aggregates.application.command.UpdateAuction;
import io.cwiekala.aggregates.domain.auction.Auction.AuctionId;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidPlacementFailure;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidWasPlaced;
import io.cwiekala.aggregates.domain.auction.AuctionEvent.WinningBidWasChangedWithNewOne;
import io.cwiekala.aggregates.utils.aggregateid.AuctioneerId;
import io.cwiekala.aggregates.utils.aggregateid.ListingId;
import io.cwiekala.aggregates.utils.aggregateid.SellerId;
import io.cwiekala.aggregates.utils.comments.AggregateRoot;
import io.cwiekala.aggregates.utils.comments.AuctionAggregate;
import io.vavr.control.Either;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
