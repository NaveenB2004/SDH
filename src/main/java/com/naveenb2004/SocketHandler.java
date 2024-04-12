package com.naveenb2004;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@Getter
public class SocketHandler {
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    @Setter
    private int bufferSize = 1024;

    public SocketHandler(@NonNull Socket socket)
            throws IOException {
        this.socket = socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    public void send(DataHandler data) throws IOException {
        outputStream.write(ProtocolHandler.serialize(data));
        outputStream.flush();
    }

    public void receive(DataHandler data) {

    }

    public void close() throws IOException {
        socket.close();
    }
}
