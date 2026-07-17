package org.redis;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

sealed interface RespValue permits RespSimpleString, RespError, RespInteger, RespBulkString, RespArray, RespNull {
    default byte[] encode() {
        return switch (this) {
            case RespSimpleString(String v) -> ("+" + v + "\r\n").getBytes(StandardCharsets.UTF_8);
            case RespError(String msg) -> ("-" + msg + "\r\n").getBytes(StandardCharsets.UTF_8);
            case RespInteger(long v) -> (":" + v + "\r\n").getBytes(StandardCharsets.UTF_8);
            case RespNull n -> "$-1\r\n".getBytes(StandardCharsets.UTF_8);
            case RespBulkString(byte[] bytes) -> {
                var header = ("$" + bytes.length + "\r\n").getBytes(StandardCharsets.UTF_8);
                var out = new ByteArrayOutputStream();
                out.writeBytes(header);
                out.writeBytes(bytes);
                out.writeBytes("\r\n".getBytes(StandardCharsets.UTF_8));
                yield out.toByteArray();
            }
            case RespArray(List<RespValue> items) -> {
                var out = new ByteArrayOutputStream();
                out.writeBytes(("*" + items.size() + "\r\n").getBytes(StandardCharsets.UTF_8));
                for (var item : items) out.writeBytes(item.encode());
                yield out.toByteArray();
            }
        };
    }

    static String debugString(RespValue v) {
        return switch (v) {
            case RespBulkString b -> "\"" + new String(b.bytes(), StandardCharsets.US_ASCII) + "\"";
            case RespSimpleString s -> s.value();
            case RespInteger i -> String.valueOf(i.value());
            case RespError e -> e.message();
            case RespArray a -> a.items().stream()
                    .map(RespValue::debugString)
                    .collect(Collectors.joining(", ", "[", "]"));
            case RespNull n -> "null";
        };
    }


}

record RespSimpleString(String value) implements RespValue {}
record RespError(String message) implements RespValue {}
record RespInteger(long value) implements RespValue {}
record RespBulkString(byte[] bytes) implements RespValue {}
record RespArray(List<RespValue> items) implements RespValue {}
record RespNull() implements RespValue {}


