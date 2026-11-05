package org.redis.protocol;

public sealed interface RespValue permits
        RespSimpleString,
        RespError,
        RespInteger,
        RespBulkString,
        RespArray,
        RespNull {
}
