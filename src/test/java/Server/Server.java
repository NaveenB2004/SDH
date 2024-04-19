package Server;

import lombok.Cleanup;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        new Server().connect(2004);
    }

    public void connect(int port) {
        new Thread(() -> {
            try {
                @Cleanup
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("Server started on port " + port);

                while (true) {
                    System.out.println("Server : Waiting for clients...");
                    Socket socket = serverSocket.accept();
                    System.out.println("Server : Client connected to server @  : " + socket.getRemoteSocketAddress());
                    new ClientsHandler(socket);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
