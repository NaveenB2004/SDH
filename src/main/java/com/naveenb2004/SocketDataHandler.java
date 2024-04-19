package com.naveenb2004;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.Synchronized;

import java.io.*;
import java.net.Socket;

public abstract class SocketDataHandler implements Closeable {
    @NonNull
    protected final Socket socket;
    private final Thread thread = new Thread(new DataProcessor(this));
    public static int ioBufferSize = 1024;
    public static int maxBodySize = 5 * 1024 * 1024;

    public SocketDataHandler(@NonNull final Socket socket) {
        this.socket = socket;
        thread.start();
    }

    @Synchronized
    public void send(@NonNull DataHandler dataHandler) throws IOException {
        OutputStream os = socket.getOutputStream();
        byte[] buffer = new byte[ioBufferSize];
        ByteArrayInputStream data = DataProcessor.serialize(dataHandler);

        @Cleanup
        BufferedInputStream bos = new BufferedInputStream(data);

        int c;
        while ((c = bos.read(buffer)) != -1) {
            os.write(buffer, 0, c);
        }
        os.flush();
    }

    public void close() throws IOException {
        if (thread.isAlive()) {
            thread.interrupt();
        }
        if (socket.isConnected()) {
            socket.close();
        }
    }

    public abstract void receive(@NonNull DataHandler update);

}
