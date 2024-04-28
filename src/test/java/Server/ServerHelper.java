package Server;

import Common.SampleObject;
import com.naveenb2004.DataHandler;
import com.naveenb2004.PreUpdateHandler.PreDataHandler;
import com.naveenb2004.PreUpdateHandler.PreUpdateHandler;
import com.naveenb2004.PreUpdateHandler.PreUpdateWatcher;
import com.naveenb2004.SocketDataHandler;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;

public class ServerHelper extends SocketDataHandler implements PreUpdateWatcher {

    public ServerHelper(@NonNull final Socket SOCKET) {
        super(SOCKET);
        PreUpdateHandler preUpdateHandler = getPRE_UPDATE_HANDLER();
        preUpdateHandler.addWatcher(this);
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(@NonNull DataHandler update) {
        System.out.println("Server : Update received!");
        System.out.println("Server : Request = " + update.getRequest());
        System.out.println("Server : Timestamp = " + update.getTimestamp());
        System.out.println("Server : DataType = " + update.getDataType());

        if (update.getDataType() == DataHandler.DataType.FILE) {
            try {
                Desktop.getDesktop().open(update.getFile());
                System.out.println("Server : File opened!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if (update.getDataType() == DataHandler.DataType.OBJECT) {
            SampleObject object = (SampleObject) update.getData();
            System.out.println("Server : Object Details = " + object.toString());
        }

        System.out.println();
    }

    @Override
    public void onPreUpdateSeen(@NonNull PreDataHandler preUpdate) {
        System.out.println("Server : Pre update received!");
        System.out.println("Server : Request = " + preUpdate.getRequest());
        System.out.println("Server : Update method = " + preUpdate.getMethod());
        System.out.println("Server : DataType = " + preUpdate.getDataType());
        System.out.println("Server : Total data size = " + preUpdate.getTotalDataSize());
        System.out.println("Server : Transferred data size = " + preUpdate.getTransferredDataSize());
        System.out.println("Server : Is completed = " + preUpdate.isCompleted());
        System.out.println();
    }

    protected void sendText() {
        System.out.println("Server : Sending Text...");
        DataHandler dataHandler = new DataHandler("/SendTextByServer");
        try {
            send(dataHandler);
            System.out.println("Server : Text sent!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
    }
}
