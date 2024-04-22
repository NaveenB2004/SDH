package Server;

import lombok.Cleanup;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public void connect(int port) {
        try {
            @Cleanup
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server : Server started on port " + port);

            while (true) {
                System.out.println("Server : Waiting for clients...");
                Socket socket = serverSocket.accept();
                System.out.println("Server : Client connected to server on  : " + socket.getRemoteSocketAddress());

                ServerHelper serverHelper = new ServerHelper(socket);

                Thread serverThread = new Thread(serverHelper);
                serverThread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
