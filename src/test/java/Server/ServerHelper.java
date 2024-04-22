package Server;

import com.naveenb2004.DataHandler;
import com.naveenb2004.SocketDataHandler;
import lombok.NonNull;

import java.io.IOException;
import java.net.Socket;

public class ServerHelper extends SocketDataHandler {

    public ServerHelper(@NonNull Socket SOCKET) {
        super(SOCKET);
    }

    @Override
    public void receive(@NonNull DataHandler update) {
        System.out.println("Server : Update received!");
        System.out.println("Server : Request = " + update.getRequest());
        System.out.println("Server : Timestamp = " + update.getTimestamp());
        System.out.println("Server : DataType = " + update.getDataType());
        System.out.println();

        if (update.getRequest().equals("/FirstMessage")) {
            System.out.println("Server : FirstMessage received!");
            System.out.println("Server : Replying to first message...");
            DataHandler dataHandler = new DataHandler("/ReplyToFirstMessage");
            try {
                send(dataHandler);
                System.out.println("Server : Reply sent!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println();
        }
    }
}
