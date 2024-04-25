package Server;

import Common.SampleObject;
import com.naveenb2004.DataHandler;
import com.naveenb2004.SocketDataHandler;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;

public class ServerHelper extends SocketDataHandler {

    public ServerHelper(@NonNull final Socket SOCKET) {
        super(SOCKET);
    }

    @SneakyThrows
    @Override
    public void receive(@NonNull DataHandler update) {
        System.out.println("Server : Update received!");
        System.out.println("Server : Request = " + update.getRequest());
        System.out.println("Server : Timestamp = " + update.getTimestamp());
        System.out.println("Server : DataType = " + update.getDataType());

        if (update.getDataType() == DataHandler.DataType.FILE) {
            while (update.getTotalDataSize() == -1L ||
                    update.getTotalDataSize() > update.getTransferredDataSize()) {
                System.out.print("Server : Download progress : " + update.getTransferredDataSize() + " / "
                        + update.getTotalDataSize() + "\r");
//                Thread.sleep(500);
            }
            System.out.println();

            try {
                Desktop.getDesktop().open(update.getFile());
                System.out.println("Server : File opened!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (update.getDataType() == DataHandler.DataType.OBJECT) {
            while (!update.isCompleted()) {
                Thread.sleep(500);
            }
            SampleObject object = (SampleObject) update.getData();
            System.out.println("Server : Object Details = " + object.toString());

            close();
        }

        System.out.println();
    }
}
