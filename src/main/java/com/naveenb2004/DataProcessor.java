package com.naveenb2004;

import lombok.Cleanup;
import lombok.NonNull;

import java.io.*;
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
        byte[] bodyBytes = dataHandler.getData();

        String header = String.format("{%s,%s,%s}",
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
        int headerSize = 8 + String.valueOf(SocketDataHandler.maxBodySize).length();
        try {
            InputStream in = sdh.socket.getInputStream();

            while (true) {
                @Cleanup
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[headerSize];
                int c;
                while (true) {
                    c = in.read(buffer);
                    baos.write(buffer, 0, c);

                    byte[] header = baos.toByteArray();
                    if (header[0] == '{' && header[headerSize - 1] == '}') {
                        String[] headerLengths = new String(Arrays.copyOfRange(header, 1, headerSize - 2),
                                StandardCharsets.UTF_8).split(",");

                        int titleLen = Integer.parseInt(headerLengths[0]);
                        int timestampLen = Integer.parseInt(headerLengths[1]);
                        int dataLen = Integer.parseInt(headerLengths[2]);

                        @Cleanup
                        ByteArrayOutputStream baos0 = new ByteArrayOutputStream();
                        @Cleanup
                        BufferedOutputStream bout = new BufferedOutputStream(baos0);
                        buffer = new byte[SocketDataHandler.ioBufferSize];
                        int total = 0;
                        while (total < titleLen + timestampLen + dataLen) {
                            c = in.read(buffer);
                            bout.write(buffer, 0, c);
                            total += c;
                        }

                        byte[] body = baos0.toByteArray();
                        DataHandler dh = new DataHandler();
                        dh.setTitle(new String(Arrays.copyOfRange(body, 0, titleLen - 1),
                                StandardCharsets.UTF_8));
                        dh.setTimestamp(new String(Arrays.copyOfRange(body, titleLen, titleLen + timestampLen - 1),
                                StandardCharsets.UTF_8));
                        dh.setData(Arrays.copyOfRange(body, titleLen + timestampLen, body.length - 1));
                        sdh.receive(dh);

                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
