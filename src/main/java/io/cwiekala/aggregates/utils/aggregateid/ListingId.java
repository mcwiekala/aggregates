package io.cwiekala.aggregates.utils.aggregateid;

import java.util.UUID;
import lombok.Value;

@Value
public class ListingId {

    private UUID value;

    public static ListingId of(UUID id) {
        return new ListingId(id);
    }

    public static ListingId generate() {
        return new ListingId(UUID.randomUUID());
    }

}