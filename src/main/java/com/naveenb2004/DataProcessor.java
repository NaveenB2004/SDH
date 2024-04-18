package com.naveenb2004;

import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

public class DataProcessor implements Runnable {
    private static final DecimalFormat TITLE = new DecimalFormat("00");
    private static final DecimalFormat TIMESTAMP = new DecimalFormat("00");
    private static final DecimalFormat DATA = new DecimalFormat("00000000");

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
    InputStream inputStream;

    public DataProcessor(@NonNull InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @SneakyThrows
    @Override
    public void run() {
// 16

    }
}
