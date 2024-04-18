package com.naveenb2004;

import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class DataProcessor implements Runnable {

    @NonNull
    protected static byte[] serialize(@NonNull DataHandler dataHandler) throws IOException {
        if (dataHandler.getTimestamp() == null) {
            LocalDateTime now = LocalDateTime.now();
            dataHandler.setTimestamp(now.toString());
        }

        byte[] titleBytes = dataHandler.getTitle().getBytes(StandardCharsets.UTF_8);
        byte[] timestampBytes = dataHandler.getTimestamp().getBytes(StandardCharsets.UTF_8);
        byte[] bodyBytes = dataHandler.getData();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
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


    }
}
