package Client;

import Common.SampleObject;
import com.naveenb2004.DataHandler;
import com.naveenb2004.SocketDataHandler;
import lombok.NonNull;

import java.io.File;
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

    protected void sendText() {
        System.out.println("Client : Sending Text...");
        DataHandler dataHandler = new DataHandler("/SendText");
        try {
            send(dataHandler);
            System.out.println("Client : Message sent!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
    }

    protected void sendFile() {
        System.out.println("Client : Sending File...");
        File file = new File("src/test/java/Common/SampleFile.jpg");
        DataHandler dataHandler = new DataHandler("/SendFile", file);
        try {
            new Thread(() -> {
                while (dataHandler.getTotalDataSize() == -1 ||
                        dataHandler.getTotalDataSize() > dataHandler.getTransferredDataSize()) {
                    System.out.println(dataHandler.getTransferredDataSize());
                }
            }).start();

            send(dataHandler);

            System.out.println("Client : File sent!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
    }

    protected void sendObject() {
        System.out.println("Client : Sending Object...");
        SampleObject object = SampleObject.builder()
                .id(123456L)
                .name("SampleObjectName")
                .age(80)
                .isMale(true)
                .build();
        DataHandler dataHandler = new DataHandler("/SendObject", object);
        try {
            send(dataHandler);
            System.out.println("Client : Object sent!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
    }
}
