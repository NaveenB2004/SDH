package com.naveenb2004;

import lombok.NonNull;

import java.nio.ByteBuffer;

public class ProtocolHandler {
    @NonNull
    protected static byte[] serialize(@NonNull DataHandler data) {
        String header = "$ BEGIN\n" +
                "$ TITLE : " + data.getTitle() + "\n" +
                "$ TIMESTAMP : " + data.getTimestamp() + "\n" +
                "$ BREAK\n";
        byte[] headerBytes = header.getBytes();
        String footer = "\n$ END";

        byte[] allBytes = new byte[headerBytes.length + data.getData().length + footer.getBytes().length];
        ByteBuffer buffer = ByteBuffer.wrap(allBytes);
        buffer.put(headerBytes);
        buffer.put(data.getData());
        buffer.put(footer.getBytes());

        return buffer.array();
    }

    protected void deserialize(@NonNull byte[] data) {
        

        for (int i = 0; i < data.length; i++) {
            if (data[i] == '$') {

            }
        }
    }
}
