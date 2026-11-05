package org.redis.commands;

import org.redis.protocol.RespError;
import org.redis.protocol.RespValue;

import java.util.List;

public final class EchoCommand {

    public static RespValue execute(List<RespValue> args) {
        if (args.size() != 1) {
            return new RespError(
                    "ERR wrong number of arguments for 'echo' command"
            );
        }

        return args.get(0);
    }
}