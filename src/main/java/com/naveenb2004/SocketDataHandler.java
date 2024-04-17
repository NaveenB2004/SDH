package com.naveenb2004;

import lombok.NonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public abstract class SocketDataHandler {
    @NonNull
    protected final Socket socket;
    protected int bufferSize = 1024;

    public SocketDataHandler(@NonNull final Socket socket, int bufferSize) {
        this.socket = socket;
        if (bufferSize != 0) {
            this.bufferSize = bufferSize;
        }
        new DataProcessor(this);
    }

    public void send(@NonNull DataHandler dataHandler) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        byte[] data = DataProcessor.serialize(dataHandler);

        int wroteBuff = 0;
        while (wroteBuff < data.length) {
            outputStream.write(data, wroteBuff, bufferSize);
            wroteBuff += bufferSize;
        }
        outputStream.flush();
    }

    public abstract void receive(@NonNull DataHandler dataHandler) throws IOException;

}
