package org.redis;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

final class RespParser {
    private BufferedInputStream in;

    public RespParser(BufferedInputStream input) {
        this.in = input;
    }

    /**
     * Takes bytes and
     */
    public RespValue parse ( ) throws IOException {
        int type = in.read();
        if (type == -1) throw new EOFException();

        return switch (type) {
            case '+' -> new RespSimpleString(readLine());
            case '-' -> new RespError(readLine());
            case ':' -> new RespInteger(Long.parseLong(readLine()));
            case '$' -> parseBulkString();
            case '*' -> parseArray();
            default -> throw new IOException("Unknown RESP type: " + (char) type);
        };
    }

    private RespValue parseArray() throws IOException {
        int len = Integer.parseInt(readLine());
        if (len == -1) return new RespNull();

        List<RespValue> items = new ArrayList<>(len);
        for (int i = 0; i < len; i++) items.add(parse());
        return new RespArray(items);
    }

    private RespValue parseBulkString() throws IOException {
        int len = Integer.parseInt(readLine());
        if (len == -1) return new RespNull();

        byte[] data = in.readNBytes(len);
        readLine();
        return new RespBulkString(data);
    }

    /**
     * Read input stream to provide the String inside it.
     * Get rid of trailing \r\n.
     */
    private String readLine() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1 && b != '\r') buf.write(b);
        in.read(); // consume \n
        return buf.toString(StandardCharsets.US_ASCII);
    }
}
