package org.redis;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.redis.RespValue.debugString;

/**
 * Bind server to a port and start handling multiple concurrent clients.
 * Includes ClientHandler as inner static class.
 */
public class RedisServer {
    private ServerSocket serverSocket;

    public void start (int port) throws IOException {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    public void stop() throws IOException {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Socket for client connection. Using this you can read data from client
     * and send data back.
     *
     * Each connection spawns a new Thread.
     */
    private static class ClientHandler extends Thread {

        private final Socket clientSocket;
        private OutputStream out;
        private BufferedInputStream in;


        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        /**
         * What happens inside new Thread.
         */
        public void run () {
            try {
                in = new BufferedInputStream(clientSocket.getInputStream());
                out = clientSocket.getOutputStream();

                RespParser parser = new RespParser(in);
                RespValue request = parser.parse();

                RespValue response = RequestHandler.handle(request);
                out.write(response.encode());

                System.out.println(debugString(request));
                out.write("+PONG\r\n".getBytes(StandardCharsets.US_ASCII));
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
