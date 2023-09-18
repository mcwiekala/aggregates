package io.cwiekala.aggregates.domain.bid;

import io.cwiekala.aggregates.utils.comments.Command;
import io.cwiekala.aggregates.utils.aggregateid.AuctioneerId;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.NonNull;
import lombok.Value;

@Command
@Value
public class PlaceBid {

    @NonNull Instant timestamp;
    @NonNull BigDecimal amount;
    @NonNull AuctioneerId auctioneerId;



}
