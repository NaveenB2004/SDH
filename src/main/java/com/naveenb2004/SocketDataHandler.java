package com.naveenb2004;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.Synchronized;

import java.io.*;
import java.net.Socket;

public abstract class SocketDataHandler implements Closeable {
    @NonNull
    protected final Socket SOCKET;
    private final Thread RECEIVER_THREAD = new Thread(new DataProcessor(this));
    public static int ioBufferSize = 1024;
    public static int maxBodySize = 5 * 1024 * 1024;

    public SocketDataHandler(@NonNull final Socket SOCKET) {
        this.SOCKET = SOCKET;
        RECEIVER_THREAD.start();
    }

    @Synchronized
    public void send(@NonNull DataHandler dataHandler) throws IOException {
        OutputStream os = SOCKET.getOutputStream();
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
        if (RECEIVER_THREAD.isAlive()) {
            RECEIVER_THREAD.interrupt();
        }
        if (SOCKET.isConnected()) {
            SOCKET.close();
        }
    }

    public abstract void receive(@NonNull DataHandler update);

}
