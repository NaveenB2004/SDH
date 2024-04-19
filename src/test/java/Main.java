import lombok.SneakyThrows;

public class Main {

    private static final int port = 2004;

    @SneakyThrows
    public static void main(String[] args) {
        new Server.Server().connect(port);

        Thread.sleep(3000);

        new Client.Client().connect(port);
        new Client.Client().connect(port);
        new Client.Client().connect(port);
    }

}
