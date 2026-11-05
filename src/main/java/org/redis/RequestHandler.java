package org.redis;

import org.redis.commands.EchoCommand;
import org.redis.protocol.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Command Dispatcher: receives a parsed RESP request, identifies a command and delegates execution to handler
 */

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
            case "ECHO" -> EchoCommand.execute(
                    items.subList(1, items.size())
            );
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