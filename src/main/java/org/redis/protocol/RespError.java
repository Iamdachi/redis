package org.redis.protocol;

import org.redis.protocol.RespValue;

public record RespError(String message) implements RespValue {}
