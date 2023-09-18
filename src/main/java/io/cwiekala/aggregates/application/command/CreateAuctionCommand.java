package io.cwiekala.aggregates.application.command;

import io.cwiekala.aggregates.domain.auction.Auction.AuctionId;
import io.cwiekala.aggregates.utils.Command;
import io.cwiekala.aggregates.utils.aggregateid.ListingId;
import io.cwiekala.aggregates.utils.aggregateid.AuctioneerId;
import io.cwiekala.aggregates.utils.aggregateid.SellerId;
import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.javamoney.moneta.Money;

@Command
@Value
@AllArgsConstructor
public class CreateAuctionCommand {

    AuctionId auctionId;
    Duration auctionLength;
    ListingId listingId;
    SellerId sellerId;
    Money startingPrice;

    public CreateAuctionCommand(Duration auctionLength, SellerId sellerId, ListingId listingId, Money startingPrice) {
        this.auctionId = AuctionId.generate();
        this.auctionLength = auctionLength;
        this.listingId = listingId;
        this.sellerId = sellerId;
        this.startingPrice = startingPrice;
    }
}
