package io.cwiekala.aggregates.utils.aggregateid;

import java.util.UUID;
import lombok.Value;

@Value
public class AuctioneerId {

    private UUID value;

    public static AuctioneerId of(UUID id) {
        return new AuctioneerId(id);
    }

    public static AuctioneerId generate() {
        return new AuctioneerId(UUID.randomUUID());
    }

}