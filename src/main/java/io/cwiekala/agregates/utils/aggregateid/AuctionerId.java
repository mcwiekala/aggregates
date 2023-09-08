package io.cwiekala.agregates.utils.aggregateid;

import java.util.UUID;
import lombok.Value;

@Value
public class AuctionerId {

    private UUID value;

    public static AuctionerId of(UUID id) {
        return new AuctionerId(id);
    }

    public static AuctionerId generate() {
        return new AuctionerId(UUID.randomUUID());
    }

}