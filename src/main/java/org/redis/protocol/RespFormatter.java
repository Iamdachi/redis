package org.redis.protocol;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public final class RespFormatter {

    private RespFormatter() {}

    public static String format(RespValue value) {
        return switch (value) {
            case RespSimpleString s -> s.value();
            case RespError e -> "ERROR: " + e.message();
            case RespInteger i -> Long.toString(i.value());

            case RespBulkString b ->
                    new String(b.bytes(), StandardCharsets.UTF_8);

            case RespArray a ->
                    a.items().stream()
                            .map(RespFormatter::format)
                            .collect(Collectors.joining(", ", "[", "]"));

            case RespNull ignored -> "null";
        };
    }
}
