package com.naveenb2004.SocketDataHandler;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.File;
import java.io.Serializable;

@Getter
public class DataHandler {
    protected static long UUIDGenerator = 0L;

    private final long UUID;
    private final String request;
    @Setter(AccessLevel.PROTECTED)
    private long timestamp;
    @Setter(AccessLevel.PROTECTED)
    private DataType dataType;
    @Setter(AccessLevel.PROTECTED)
    private Serializable data;
    @Setter(AccessLevel.PROTECTED)
    private File file;

    @Getter
    public enum DataType {
        NONE("0"),
        FILE("1"),
        OBJECT("2");

        private final String value;

        DataType(String value) {
            this.value = value;
        }

        static DataType getType(@NonNull String value) {
            switch (value) {
                case "1":
                    return FILE;
                case "2":
                    return OBJECT;
                default:
                    return NONE;
            }
        }
    }

    public DataHandler(@NonNull String request) {
        this.request = request;
        this.dataType = DataType.NONE;
        UUID = UUIDGenerator++;
    }

    public DataHandler(@NonNull String request,
                       @NonNull Serializable data) {
        this.request = request;
        this.dataType = DataType.OBJECT;
        this.data = data;
        UUID = UUIDGenerator++;
    }

    public DataHandler(@NonNull String request,
                       @NonNull File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("Invalid file!");
        }
        this.request = request;
        this.dataType = DataType.FILE;
        this.file = file;
        UUID = UUIDGenerator++;
    }

    protected static long timestamp() {
        return System.currentTimeMillis();
    }
}
