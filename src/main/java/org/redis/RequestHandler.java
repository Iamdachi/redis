package org.redis;

import java.nio.charset.StandardCharsets;
import java.util.List;

final class RequestHandler {

    static RespValue handle(RespValue request) {
        // RESP client requests are always arrays of bulk strings
        if (!(request instanceof RespArray(List<RespValue> items)) || items.isEmpty()) {
            return new RespError("ERR invalid request");
        }

        if (!(items.get(0) instanceof RespBulkString(byte[] cmdBytes))) {
            return new RespError("ERR invalid command format");
        }

        String command = new String(cmdBytes, StandardCharsets.US_ASCII).toUpperCase();

        return switch (command) {
            case "ECHO" -> handleEcho(items);
            case "PING" -> new RespSimpleString("PONG");
            default -> new RespError("ERR unknown command '" + command + "'");
        };
    }

    private static RespValue handleEcho(List<RespValue> items) {
        if (items.size() != 2) {
            return new RespError("ERR wrong number of arguments for 'echo' command");
        }
        return items.get(1); // return the bulk string arg as-is
    }
}