package io.cwiekala.aggregates.domain.auction;

import io.cwiekala.aggregates.domain.auction.AuctionEvent.BidWasPlaced;
import io.cwiekala.aggregates.application.command.CreateAuctionCommand;
import io.cwiekala.aggregates.application.command.UpdateAuction;
import io.cwiekala.aggregates.domain.bid.BidPlacementFailure;
import io.cwiekala.aggregates.domain.bid.BidWasPlacedOLD;
import io.cwiekala.aggregates.utils.AggregateRoot;
import io.cwiekala.aggregates.utils.aggregateid.ListingId;
import io.cwiekala.aggregates.utils.aggregateid.MemberId;
import io.vavr.control.Either;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.javamoney.moneta.Money;

@AggregateRoot
@Getter
public class Auction {

    @EmbeddedId
    private AuctionId id;
    LocalDateTime startDate;
    LocalDateTime endDate;
    ListingId listingId;
    MemberId memberId;
    Money startingPrice;

    public Auction(Duration auctionLength, MemberId memberId, ListingId listingId, Money startingPrice) {
        LocalDateTime now = LocalDateTime.now();
        this.id = AuctionId.generate();
        this.startDate = now; // as params?
        this.endDate = now.plus(auctionLength);
        this.listingId = listingId;
        this.memberId = memberId;
        this.startingPrice = startingPrice;
    }

    // questions and answers?

    public void handle(CreateAuctionCommand command) {
        //
    }

    public Either<BidPlacementFailure, BidWasPlacedOLD> handle(BidWasPlaced event) {
        // check end date
        return null;
    }

    public void handle(UpdateAuction command) {
        // what can be updated?
        // shipment type and payment can be only added
    }

    public Money getCurrentPrice() {
        return null;
    }


        @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Data
    public static class AuctionId {

        @Column(name = "id", columnDefinition = "UUID")
        private UUID value;

        public static AuctionId of(UUID id) {
            return new AuctionId(id);
        }

        public static AuctionId generate() {
            return new AuctionId(UUID.randomUUID());
        }

    }
}
