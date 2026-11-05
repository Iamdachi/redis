package org.redis.protocol;

public record RespInteger(long value) implements RespValue {
}
