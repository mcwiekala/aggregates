package io.cwiekala.agregates.domain.bid;

import io.cwiekala.agregates.utils.Command;
import io.cwiekala.agregates.utils.aggregateid.AuctionerId;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.NonNull;
import lombok.Value;

@Command
@Value
public class PlaceBid {

    @NonNull Instant timestamp;
    @NonNull BigDecimal amount;
    @NonNull AuctionerId auctionerId;



}
