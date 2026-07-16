package org.redis;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        RedisServer server = new RedisServer();
        server.start(5555);
    }
}