package com.naveenb2004;

import lombok.NonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public abstract class SocketDataHandler {
    @NonNull
    protected final Socket socket;
    protected static int ioBufferSize = 1024;
    protected static int maxBodySize = 20 * 1024 * 1024;

    public SocketDataHandler(@NonNull final Socket socket) {
        this.socket = socket;
        new DataProcessor(this);
    }

    public void send(@NonNull DataHandler dataHandler) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        byte[] data = DataProcessor.serialize(dataHandler);

        for (int wroteBuff = 0; wroteBuff < data.length; wroteBuff += ioBufferSize) {
            if (data.length >= ioBufferSize) {
                outputStream.write(data, wroteBuff, ioBufferSize);
            } else {
                outputStream.write(data, wroteBuff, ioBufferSize - wroteBuff);
            }
        }
        outputStream.flush();
    }

    public abstract void receive(@NonNull DataHandler update) throws IOException;

}
