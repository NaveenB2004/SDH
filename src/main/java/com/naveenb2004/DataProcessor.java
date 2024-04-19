package com.naveenb2004;

import lombok.Cleanup;
import lombok.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

public class DataProcessor implements Runnable {
    private static final DecimalFormat TITLE = new DecimalFormat("00");
    private static final DecimalFormat TIMESTAMP = new DecimalFormat("00");
    private static final DecimalFormat DATA
            = new DecimalFormat("0".repeat(String.valueOf(SocketDataHandler.maxBodySize).length()));

    @NonNull
    protected static ByteArrayInputStream serialize(@NonNull DataHandler dataHandler) throws IOException {
        byte[] titleBytes = dataHandler.getTitle().getBytes(StandardCharsets.UTF_8);
        byte[] timestampBytes = dataHandler.getTimestamp().getBytes(StandardCharsets.UTF_8);
        byte[] bodyBytes = dataHandler.getData();

        String header = String.format("%s%s%s",
                TITLE.format(titleBytes.length),
                TIMESTAMP.format(timestampBytes.length),
                DATA.format(bodyBytes.length));

        @Cleanup
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(header.getBytes(StandardCharsets.UTF_8));
        baos.write(titleBytes);
        baos.write(timestampBytes);
        baos.write(bodyBytes);

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
        int headerSize = 8 + String.valueOf(SocketDataHandler.maxBodySize).length();
        try {
            InputStream in = sdh.socket.getInputStream();

            int c;
            while ((c = in.read()) != -1) {
                System.out.print(c);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
