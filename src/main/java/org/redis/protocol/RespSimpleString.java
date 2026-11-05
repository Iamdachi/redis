package org.redis.protocol;

public record RespSimpleString(String value) implements RespValue {

    public RespSimpleString {
        if (value == null) {
            throw new IllegalArgumentException("Simple string cannot be null");
        }

        if (value.contains("\r") || value.contains("\n")) {
            throw new IllegalArgumentException(
                    "Simple string cannot contain CR or LF"
            );
        }
    }
}