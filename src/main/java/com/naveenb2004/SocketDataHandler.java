package com.naveenb2004;

import lombok.Cleanup;
import lombok.NonNull;

import java.io.*;
import java.net.Socket;

public abstract class SocketDataHandler implements Closeable {
    @NonNull
    protected final Socket socket;
    public static int ioBufferSize = 1024;
    public static int maxBodySize = 20 * 1024 * 1024;
    protected final BufferedOutputStream bos;

    public SocketDataHandler(@NonNull final Socket socket) throws IOException {
        this.socket = socket;
        bos = new BufferedOutputStream(socket.getOutputStream());
        new DataProcessor(this);
    }

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
        if (bos != null) {
            bos.close();
        }
        if (socket.isConnected()) {
            socket.close();
        }
    }

    public abstract void receive(@NonNull DataHandler update) throws IOException;

}
