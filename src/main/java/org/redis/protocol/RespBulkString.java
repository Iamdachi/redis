package org.redis.protocol;

import java.util.Arrays;

public record RespBulkString(byte[] bytes) implements RespValue {

    public RespBulkString {
        bytes = Arrays.copyOf(bytes, bytes.length);
    }

    @Override
    public byte[] bytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }
}
