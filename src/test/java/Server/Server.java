package Server;

import lombok.Cleanup;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public void connect(int port) {
        new Thread(() -> {
            try {
                @Cleanup
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("Server started on port " + port);

                int i = 1;
                while (true) {
                    Socket socket = serverSocket.accept();
                    new ClientsHandler(socket);
                    System.out.println("Client connected to server @ id : " + i++);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
