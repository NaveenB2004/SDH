package Client;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public void connect(int port) {
        try {
            Socket socket = new Socket("localhost", port);
            System.out.println("Client : Connected to server on " + socket.getRemoteSocketAddress());

            ClientHelper clientHelper = new ClientHelper(socket);

            Thread clientThread = new Thread(clientHelper);
            clientThread.start();

            clientHelper.sendText();
            Thread.sleep(3000);

            clientHelper.sendFile();
            Thread.sleep(3000);

            clientHelper.sendObject();
            Thread.sleep(3000);

            clientHelper.close();
            System.out.println("Client : Disconnected from server " + socket.getRemoteSocketAddress());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
