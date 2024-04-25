import com.naveenb2004.SocketDataHandler;
import lombok.SneakyThrows;

import java.io.File;

public class Main {

    public static final int port = 2004;

    @SneakyThrows
    public static void main(String[] args) {
        SocketDataHandler.setDefaultBufferSize(1024L);
        SocketDataHandler.setTempFolder(new File("Temp"));

        new Server.Server().connect(port);
        Thread.sleep(3000);
        new Client.Client().connect(port);
    }

}
