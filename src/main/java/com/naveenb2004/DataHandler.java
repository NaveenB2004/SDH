package com.naveenb2004;

import lombok.Data;
import lombok.NonNull;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Data
public class DataHandler {
    @NonNull
    private String title;
    private String timestamp;
    @NonNull
    private byte[] data;

    public void setTitle(@NonNull String title) {
        if (title.getBytes(StandardCharsets.UTF_8).length > 99) {
            throw new RuntimeException("Title is too long (max 99 bytes)!");
        }
        this.title = title;
    }

    public void setTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            timestamp = now.toString();
        } else {
            try {
                LocalDateTime.parse(timestamp);
            } catch (DateTimeParseException e) {
                throw new RuntimeException("Timestamp is invalid (should be a LocalDateTime)!");
            }
        }
        this.timestamp = timestamp;
    }

    public void setData(@NonNull byte[] data) {
        if (data.length > SocketDataHandler.maxBodySize) {
            throw new RuntimeException("Data is too long (max " + SocketDataHandler.maxBodySize + " bytes)!");
        }
        this.data = data;
    }
}
