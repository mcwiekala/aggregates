package io.cwiekala.aggregates.domain.auction;

import io.cwiekala.aggregates.domain.auction.command.CreateAuction;
import io.cwiekala.aggregates.domain.auction.command.PlaceBid;
import io.cwiekala.aggregates.domain.auction.command.UpdateAuction;
import io.cwiekala.aggregates.utils.AggregateRoot;
import io.cwiekala.aggregates.utils.aggregateid.ListingId;
import io.cwiekala.aggregates.utils.aggregateid.MemberId;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AggregateRoot
public class Auction {

    @EmbeddedId
    private AuctionId id;
    LocalDateTime startDate;
    LocalDateTime endDate;
    ListingId listingId;
    MemberId memberId;

    public Auction(Duration auctionLength, MemberId memberId, ListingId listingId) {
        LocalDateTime now = LocalDateTime.now();
        this.id = AuctionId.generate();
        this.startDate = now; // as params?
        this.endDate = now.plus(auctionLength);
        this.listingId = listingId;
        this.memberId = memberId;
    }

    // questions and answers?

    void handle(CreateAuction command) {
        //
    }

    void handle(PlaceBid command) {
        // check end date
    }

    void handle(UpdateAuction command) {
        // what can be updated?
        // shipment type and payment can be only added
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
