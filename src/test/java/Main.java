import com.naveenb2004.SocketDataHandler;
import lombok.SneakyThrows;

public class Main {

    private static final int port = 2004;

    @SneakyThrows
    public static void main(String[] args) {
        SocketDataHandler.ioBufferSize = 1024;
        SocketDataHandler.maxBodySize = 5 * 1024 * 1024;

        new Server.Server().connect(port);

        Thread.sleep(3000);

        new Client.Client().connect(port);
        new Client.Client().connect(port);
        new Client.Client().connect(port);
    }

}
