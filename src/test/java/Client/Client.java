package Client;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public void connect(int port) {
        try {
            Socket socket = new Socket("localhost", port);
            System.out.println("Connected to " + socket.getRemoteSocketAddress());
            new ServerHandler(socket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
