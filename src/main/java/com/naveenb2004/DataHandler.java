package com.naveenb2004;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.File;
import java.io.Serializable;

@Getter
public class DataHandler {
    private final String request;
    @Setter(AccessLevel.PROTECTED)
    private long timestamp;
    @Setter(AccessLevel.PROTECTED)
    private DataType dataType;
    @Setter(AccessLevel.PROTECTED)
    private Serializable data;
    @Setter(AccessLevel.PROTECTED)
    private File file;
    @Setter(AccessLevel.PROTECTED)
    private long totalDataSize = -1L;
    @Setter(AccessLevel.PROTECTED)
    private long transferredDataSize;

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
    }

    public DataHandler(@NonNull String request,
                       @NonNull Serializable data) {
        this.request = request;
        this.dataType = DataType.OBJECT;
        this.data = data;
    }

    public DataHandler(@NonNull String request,
                       @NonNull File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("Invalid file!");
        }
        this.request = request;
        this.dataType = DataType.FILE;
        this.file = file;
    }

    protected static long timestamp() {
        return System.currentTimeMillis();
    }
}
