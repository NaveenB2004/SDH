package com.naveenb2004;

import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class DataProcessor implements Runnable {
    protected static final byte[] startMarker = "$BEGIN$".getBytes(StandardCharsets.UTF_8);
    protected static final byte[] breakMarker = "$BREAK$".getBytes(StandardCharsets.UTF_8);
    protected static final byte[] endMarker = "$END$".getBytes(StandardCharsets.UTF_8);

    @NonNull
    protected static byte[] serialize(@NonNull DataHandler dataHandler) throws IOException {
        if (dataHandler.getTimestamp() == null) {
            LocalDateTime now = LocalDateTime.now();
            dataHandler.setTimestamp(now.toString());
        }

        byte[] titleBytes = (dataHandler.getTitle() + "$").getBytes(StandardCharsets.UTF_8);
        byte[] timestampBytes = dataHandler.getTimestamp().getBytes(StandardCharsets.UTF_8);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(startMarker);
        baos.write(titleBytes);
        baos.write(timestampBytes);
        baos.write(breakMarker);
        baos.write(dataHandler.getData());
        baos.write(endMarker);

        return baos.toByteArray();
    }

    SocketDataHandler parent;

    public DataProcessor(@NonNull SocketDataHandler parent) {
        this.parent = parent;
    }

    @SneakyThrows
    @Override
    public void run() {
        InputStream in = parent.socket.getInputStream();
        ByteArrayOutputStream baos = null;
        DataHandler dh = null;
        int status = 0;
        while (parent.socket.isConnected()) {
            switch (status) {
                case 0:
                    byte[] readBegin = new byte[startMarker.length];
                    in.read(readBegin, 0, startMarker.length);
                    if (readBegin == startMarker) {
                        dh = new DataHandler();
                        baos = new ByteArrayOutputStream();
                        status = 1;
                    }
                    break;
                case 1:
                    byte[] readBreak = new byte[breakMarker.length];
                    in.read(readBreak, 0, breakMarker.length);
                    if (readBreak == breakMarker) {
                        String header = baos.toString(StandardCharsets.UTF_8);
                        dh.setTitle(header.split("\\$")[0]);
                        dh.setTimestamp(header.split("\\$")[1]);
                        baos = new ByteArrayOutputStream();
                        status = 2;
                    } else {
                        baos.write(readBreak);
                    }
                    break;
                case 2:
                    byte[] readEnd = new byte[endMarker.length];
                    in.read(readEnd, 0, endMarker.length);
                    if (readEnd == endMarker) {
                        dh.setData(baos.toByteArray());
                        parent.receive(dh);
                        status = 0;
                    } else {
                        baos.write(readEnd);
                    }
                    break;
            }
        }
    }
}
