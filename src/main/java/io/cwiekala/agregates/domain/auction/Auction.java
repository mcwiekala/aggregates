package io.cwiekala.agregates.domain.auction;

import io.cwiekala.agregates.domain.auction.command.CreateAuction;
import io.cwiekala.agregates.domain.auction.command.PlaceBid;
import io.cwiekala.agregates.domain.auction.command.UpdateAuction;
import io.cwiekala.agregates.utils.AggregateRoot;
import io.cwiekala.agregates.utils.aggregateid.CategoryId;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AggregateRoot
class Auction {

    @EmbeddedId
    private AuctionId id;
    private CategoryId categoryId;
    LocalDateTime startDate;
    LocalDateTime endDate;
    List<ShipmentOption> shipmentOptions;
    List<PaymentOption> paymentOptions;

    // questions and answers?

    void handle(CreateAuction command){
        //
    }

    void handle(PlaceBid command){
        // check end date
    }

    void handle(UpdateAuction command){
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
