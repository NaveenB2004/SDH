package com.naveenb2004;

import lombok.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Arrays;

@RequiredArgsConstructor
public abstract class SocketDataHandler implements Runnable, AutoCloseable {
    @Getter
    @NonNull
    private final Socket SOCKET;
    @Getter
    @Setter
    private static int defaultBufferSize = 1024;
    @Getter
    @Setter
    private static int maxDataSize = 5 * 1024 * 1024;

    public abstract void receive(@NonNull DataHandler update);

    @Synchronized
    public void send(@NonNull DataHandler dataHandler) throws IOException {
        OutputStream os = SOCKET.getOutputStream();
        byte[] buffer = new byte[defaultBufferSize];
        ByteArrayInputStream data = serialize(dataHandler);

        @Cleanup
        BufferedInputStream bos = new BufferedInputStream(data);

        int c;
        while ((c = bos.read(buffer)) != -1) {
            os.write(buffer, 0, c);
        }
        os.flush();
    }

    private static final DecimalFormat TITLE = new DecimalFormat("00");
    private static final DecimalFormat TIMESTAMP = new DecimalFormat("00");
    private static final DecimalFormat DATA
            = new DecimalFormat("0".repeat(String.valueOf(SocketDataHandler.maxDataSize).length()));

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

    @Override
    public void run() {
        final int headerSize = 4 + String.valueOf(SocketDataHandler.maxDataSize).length();
        try {
            InputStream in = SOCKET.getInputStream();
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
                byte[] buffer = new byte[SocketDataHandler.defaultBufferSize];
                while (bodySize > 0) {
                    int c;
                    if (bodySize >= SocketDataHandler.defaultBufferSize) {
                        c = in.read(buffer);
                        out.write(buffer, 0, c);
                        bodySize -= SocketDataHandler.defaultBufferSize;
                    } else {
                        byte[] finBuff = new byte[bodySize];
                        c = in.read(finBuff);
                        out.write(finBuff, 0, c);
                        bodySize = 0;
                    }
                }

                byte[] data = out.toByteArray();
                int dest = headerData[0];
                String title = new String(Arrays.copyOfRange(data, 0, dest), StandardCharsets.UTF_8);
                dest += headerData[1];
                String timestamp = new String(Arrays.copyOfRange(data, headerData[0], dest), StandardCharsets.UTF_8);
                dest += headerData[2];
                byte[] body = Arrays.copyOfRange(data, headerData[0] + headerData[1], dest);
                receive(DataHandler.builder()
                        .title(title)
                        .dataType(DataHandler.DataType.RAW_BYTES)
                        .data(data)
                        .build());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() throws IOException {
        if (SOCKET.isConnected()) {
            SOCKET.close();
        }
    }
}
