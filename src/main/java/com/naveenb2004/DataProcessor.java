package com.naveenb2004;

import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class DataProcessor implements Runnable {
    protected static final byte[] startMarker = "$BEGIN$".getBytes(StandardCharsets.UTF_8);
    protected static final byte[] breakMarker = "$BREAK$".getBytes(StandardCharsets.UTF_8);
    protected static final byte[] endMarker = "$END$".getBytes(StandardCharsets.UTF_8);

    @NonNull
    protected static byte[] serialize(@NonNull DataHandler dataHandler) {
        if (dataHandler.getTimestamp() == null) {
            LocalDateTime now = LocalDateTime.now();
            dataHandler.setTimestamp(now.toString());
        }

        byte[] titleBytes = dataHandler.getTitle().getBytes(StandardCharsets.UTF_8);
        byte[] timestampBytes = dataHandler.getTimestamp().getBytes(StandardCharsets.UTF_8);

        byte[] bytes = new byte[startMarker.length + breakMarker.length + endMarker.length + titleBytes.length +
                timestampBytes.length + dataHandler.getData().length];
        int destPos = 0;
        System.arraycopy(startMarker, 0, bytes, destPos, startMarker.length);
        destPos += startMarker.length;
        System.arraycopy(titleBytes, 0, bytes, destPos, titleBytes.length);
        destPos += titleBytes.length;
        System.arraycopy(timestampBytes, 0, bytes, destPos, timestampBytes.length);
        destPos += timestampBytes.length;
        System.arraycopy(breakMarker, 0, bytes, destPos, breakMarker.length);
        destPos += breakMarker.length;
        System.arraycopy(dataHandler.getData(), 0, bytes, destPos, dataHandler.getData().length);
        destPos += dataHandler.getData().length;
        System.arraycopy(endMarker, 0, bytes, destPos, endMarker.length);

        return bytes;
    }

    SocketDataHandler parent;

    public DataProcessor(@NonNull SocketDataHandler parent) {
        this.parent = parent;
    }

    @SneakyThrows
    @Override
    public void run() {
        InputStream in = parent.socket.getInputStream();
        byte[] data = null;
        while (parent.socket.isConnected()) {

        }
    }
}
