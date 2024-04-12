package com.naveenb2004;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@Getter
public class SocketHandler implements Closeable {
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final ProtocolHandler protocolHandler;
    @Setter
    private int bufferSize = 1024;

    public SocketHandler(@NonNull Socket socket)
            throws IOException {
        this.socket = socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        protocolHandler = new ProtocolHandler();
    }

    public void send(DataHandler dataSet) throws IOException {
        byte[] data = ProtocolHandler.serialize(dataSet);

        int i = 0;
        while (i < data.length) {
            outputStream.write(data, 0, bufferSize);
            i += bufferSize;
        }
        outputStream.flush();
    }

    public void receive(DataHandler data) {

    }

    public void close() throws IOException {
        socket.close();
    }
}
