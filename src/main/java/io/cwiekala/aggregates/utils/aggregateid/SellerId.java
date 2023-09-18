package io.cwiekala.aggregates.utils.aggregateid;

import java.util.UUID;
import lombok.Value;

@Value
public class SellerId {

    private UUID value;

    public static SellerId of(UUID id) {
        return new SellerId(id);
    }

    public static SellerId generate() {
        return new SellerId(UUID.randomUUID());
    }

}