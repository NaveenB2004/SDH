package com.naveenb2004;

import lombok.NonNull;
import lombok.SneakyThrows;

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
    protected static byte[] serialize(@NonNull DataHandler dataHandler) throws IOException {
        byte[] titleBytes = dataHandler.getTitle().getBytes(StandardCharsets.UTF_8);
        byte[] timestampBytes = dataHandler.getTimestamp().getBytes(StandardCharsets.UTF_8);
        byte[] bodyBytes = dataHandler.getData();

        String header = String.format("{%s,%s,%s}",
                TITLE.format(titleBytes.length),
                TIMESTAMP.format(timestampBytes.length),
                DATA.format(bodyBytes.length));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(header.getBytes(StandardCharsets.UTF_8));
        baos.write(titleBytes);
        baos.write(timestampBytes);
        baos.write(bodyBytes);

        return baos.toByteArray();
    }

    @NonNull
    SocketDataHandler sdh;

    public DataProcessor(@NonNull SocketDataHandler sdh) {
        this.sdh = sdh;
    }

    @SneakyThrows
    @Override
    public void run() {
        int headerSize = 8 + String.valueOf(SocketDataHandler.maxBodySize).length();
        InputStream inputStream = sdh.socket.getInputStream();
        while (inputStream.available() != 0) {
            byte[] buffer = new byte[headerSize];
            inputStream.read(buffer, 0, headerSize);
            System.out.println("bytes received!");
            if (buffer[0] == '{' && buffer[headerSize - 1] == '}') {
                String content = new String(Arrays.copyOfRange(buffer, 1, headerSize - 2),
                        StandardCharsets.UTF_8);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                int titleSize = Integer.parseInt(content.split(",")[0]);
                int timestampSize = Integer.parseInt(content.split(",")[1]);
                int dataSize = Integer.parseInt(content.split(",")[2]);
                int totalSize = titleSize + timestampSize + dataSize;

                int i = 0;
                while (i < totalSize) {
                    byte[] chunk = new byte[SocketDataHandler.ioBufferSize];
                    inputStream.read(chunk, 0, chunk.length);
                    baos.write(chunk);
                    i += SocketDataHandler.ioBufferSize;
                }

                byte[] body = baos.toByteArray();
                String title = new String(Arrays.copyOfRange(body, 0, titleSize - 1),
                        StandardCharsets.UTF_8);
                String timestamp = new String(Arrays.copyOfRange(body, titleSize - 1, titleSize + timestampSize - 1),
                        StandardCharsets.UTF_8);
                byte[] data = Arrays.copyOfRange(body, titleSize + timestampSize - 1, totalSize - 1);

                DataHandler dataHandler = new DataHandler();
                dataHandler.setTitle(title);
                dataHandler.setTimestamp(timestamp);
                dataHandler.setData(data);
                sdh.receive(dataHandler);
            }
        }
    }
}
