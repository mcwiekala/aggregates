package io.cwiekala.aggregates.utils.aggregateid;

import java.util.UUID;
import lombok.Value;

@Value
public class CategoryId {

    private UUID value;

    public static CategoryId of(UUID id) {
        return new CategoryId(id);
    }

    public static CategoryId generate() {
        return new CategoryId(UUID.randomUUID());
    }

}