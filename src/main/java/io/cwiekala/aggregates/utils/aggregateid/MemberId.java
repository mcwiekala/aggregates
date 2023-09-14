package io.cwiekala.aggregates.utils.aggregateid;

import java.util.UUID;
import lombok.Value;

@Value
public class MemberId {

    private UUID value;

    public static MemberId of(UUID id) {
        return new MemberId(id);
    }

    public static MemberId generate() {
        return new MemberId(UUID.randomUUID());
    }

}