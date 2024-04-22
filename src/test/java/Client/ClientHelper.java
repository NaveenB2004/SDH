package Client;

import com.naveenb2004.DataHandler;
import com.naveenb2004.SocketDataHandler;
import lombok.NonNull;

import java.io.IOException;
import java.net.Socket;

public class ClientHelper extends SocketDataHandler {

    public ClientHelper(@NonNull Socket SOCKET) {
        super(SOCKET);
    }

    @Override
    public void receive(@NonNull DataHandler update) {
        System.out.println("Client : Update received!");
        System.out.println("Client : Request = " + update.getRequest());
        System.out.println("Client : Timestamp = " + update.getTimestamp());
        System.out.println("Client : DataType = " + update.getDataType());
        System.out.println();
    }

    protected void sendFirstMessage() {
        System.out.println("Client : Sending first message...");
        DataHandler dataHandler = new DataHandler("/FirstMessage");
        try {
            send(dataHandler);
            System.out.println("Client : Message sent!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
    }
}
