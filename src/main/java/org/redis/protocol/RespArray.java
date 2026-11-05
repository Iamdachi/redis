package org.redis.protocol;

import java.util.List;

public record RespArray(List<RespValue> items) implements RespValue {

    public RespArray {
        items = List.copyOf(items);
    }
}
