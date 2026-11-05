package org.redis.protocol;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public final class RespEncoder {

    private RespEncoder() {}

    public static void write(RespValue value, OutputStream out)
            throws IOException {

        switch (value) {
            case RespSimpleString s -> {
                writeAscii(out, "+" + s.value() + "\r\n");
            }

            case RespError e -> {
                writeAscii(out, "-" + e.message() + "\r\n");
            }

            case RespInteger i -> {
                writeAscii(out, ":" + i.value() + "\r\n");
            }

            case RespBulkString b -> {
                byte[] bytes = b.bytes();

                writeAscii(out, "$" + bytes.length + "\r\n");
                out.write(bytes);
                writeAscii(out, "\r\n");
            }

            case RespArray a -> {
                writeAscii(out, "*" + a.items().size() + "\r\n");

                for (RespValue item : a.items()) {
                    write(item, out);
                }
            }

            case RespNull ignored -> {
                writeAscii(out, "$-1\r\n");
            }
        }
    }

    private static void writeAscii(OutputStream out, String value)
            throws IOException {
        out.write(value.getBytes(StandardCharsets.US_ASCII));
    }
}
