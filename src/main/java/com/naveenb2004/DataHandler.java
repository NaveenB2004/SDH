package com.naveenb2004;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Getter
@Setter
public class DataHandler {
    @NonNull
    private String title;
    private String timestamp; // LocalDateTime format (2024-04-18T07:52:30.328585500)
    @NonNull
    private byte[] data;

    public void setTitle(@NonNull String title) throws Exception {
        if (title.getBytes(StandardCharsets.UTF_8).length > 99) {
            throw new Exception("Title is too long (max 99 bytes)!");
        }
        this.title = title;
    }

    public void setTimestamp(String timestamp) throws DateTimeParseException {
        if (timestamp == null || timestamp.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            timestamp = now.toString();
        } else {
            try {
                LocalDateTime.parse(timestamp);
            } catch (DateTimeParseException e) {
                throw new DateTimeParseException("Timestamp is invalid (should be in 'LocalDateTime' format)!",
                        timestamp, 0);
            }
        }
        this.timestamp = timestamp;
    }

    public void setData(@NonNull byte[] data) throws Exception {
        if (data.length > SocketDataHandler.maxBodySize) {
            throw new Exception("Data is too long (max " + SocketDataHandler.maxBodySize + " bytes)!");
        }
        this.data = data;
    }
}
