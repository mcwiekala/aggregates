package io.cwiekala.aggregates.application.command;

import io.cwiekala.aggregates.utils.Command;
import io.cwiekala.aggregates.utils.aggregateid.ListingId;
import io.cwiekala.aggregates.utils.aggregateid.MemberId;
import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.javamoney.moneta.Money;

@Command
@Value
@AllArgsConstructor
public class CreateAuctionCommand {

    Duration auctionLength;
    ListingId listingId;
    MemberId sellerId;
    Money startingPrice;

    public CreateAuctionCommand(Duration auctionLength, MemberId sellerId, ListingId listingId, Money startingPrice) {
        this.auctionLength = auctionLength;
        this.listingId = listingId;
        this.sellerId = sellerId;
        this.startingPrice = startingPrice;
    }
}
