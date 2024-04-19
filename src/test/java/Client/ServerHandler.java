package Client;

import com.naveenb2004.DataHandler;
import com.naveenb2004.SocketDataHandler;
import lombok.NonNull;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerHandler extends SocketDataHandler {
    public ServerHandler(@NonNull Socket socket) {
        super(socket);
        sendSamples();
    }

    @Override
    public void receive(@NonNull DataHandler update) {
        System.out.println("Update received <server>!");
        System.out.println("Title : " + update.getTitle());
        System.out.println("Timestamp : " + update.getTimestamp());
        System.out.println("Data : " + new String(update.getData(), StandardCharsets.UTF_8));
        System.out.println();
    }

    public void sendSamples() {
        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                DataHandler dh = new DataHandler();
                dh.setTitle("/testData-" + i);
                dh.setTimestamp(null);
                dh.setData("Sample Body".getBytes(StandardCharsets.UTF_8));

                try {
                    send(dh);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
