package com.naveenb2004;

import lombok.Cleanup;
import lombok.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Arrays;

public class DataProcessor implements Runnable {
    private static final DecimalFormat TITLE = new DecimalFormat("00");
    private static final DecimalFormat TIMESTAMP = new DecimalFormat("00");
    private static final DecimalFormat DATA
            = new DecimalFormat("0".repeat(String.valueOf(SocketDataHandler.maxBodySize).length()));

    @NonNull
    protected static ByteArrayInputStream serialize(@NonNull DataHandler dataHandler) throws IOException {
        byte[] titleBytes = dataHandler.getTitle().getBytes(StandardCharsets.UTF_8);
        byte[] timestampBytes = dataHandler.getTimestamp().getBytes(StandardCharsets.UTF_8);
        byte[] dataBytes = dataHandler.getData();

        String header = String.format("%s%s%s",
                TITLE.format(titleBytes.length),
                TIMESTAMP.format(timestampBytes.length),
                DATA.format(dataBytes.length));

        @Cleanup
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(header.getBytes(StandardCharsets.UTF_8));
        baos.write(titleBytes);
        baos.write(timestampBytes);
        baos.write(dataBytes);

        @Cleanup
        ByteArrayInputStream bain = new ByteArrayInputStream(baos.toByteArray());

        return bain;
    }

    @NonNull
    SocketDataHandler sdh;

    public DataProcessor(@NonNull SocketDataHandler sdh) {
        this.sdh = sdh;
    }

    @Override
    public void run() {
        System.out.println("Receiver initialized!");
        final int headerSize = 4 + String.valueOf(SocketDataHandler.maxBodySize).length();
        try {
            InputStream in = sdh.socket.getInputStream();
            System.out.println("Lib : Receiving...");
            while (true) {
                // read meta buffer
                byte[] header = new byte[headerSize];
                in.read(header);

                int[] headerData = {
                        Integer.parseInt(new String(Arrays.copyOfRange(header, 0, 2), StandardCharsets.UTF_8)),
                        Integer.parseInt(new String(Arrays.copyOfRange(header, 2, 4), StandardCharsets.UTF_8)),
                        Integer.parseInt(new String(Arrays.copyOfRange(header, 4, headerSize), StandardCharsets.UTF_8))
                };

                int bodySize = headerData[0] + headerData[1] + headerData[2];
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[SocketDataHandler.ioBufferSize];
                while (bodySize > 0) {
                    int c;
                    if (bodySize >= SocketDataHandler.ioBufferSize) {
                        c = in.read(buffer);
                        out.write(buffer, 0, c);
                        bodySize -= SocketDataHandler.ioBufferSize;
                    } else {
                        byte[] finBuff = new byte[bodySize];
                        c = in.read(finBuff);
                        out.write(finBuff, 0, c);
                        bodySize = 0;
                    }
                }

                byte[] data = out.toByteArray();
                DataHandler dh = new DataHandler();
                int dest = headerData[0];
                dh.setTitle(new String(Arrays.copyOfRange(data, 0, dest), StandardCharsets.UTF_8));
                dest += headerData[1];
                dh.setTimestamp(new String(Arrays.copyOfRange(data, headerData[0], dest), StandardCharsets.UTF_8));
                dest += headerData[2];
                dh.setData(Arrays.copyOfRange(data, headerData[0] + headerData[1], dest));
                sdh.receive(dh);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
