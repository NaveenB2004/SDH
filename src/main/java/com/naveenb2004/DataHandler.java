package com.naveenb2004;

import lombok.*;

import java.io.File;
import java.io.Serializable;

@Getter
@Setter
@RequiredArgsConstructor
public class DataHandler {
    @NonNull
    private final String request;
    @Setter(AccessLevel.PROTECTED)
    private long timestamp;
    @NonNull
    private DataType dataType;
    private Serializable data;
    private File dataLocation;

    public enum DataType {
        NONE("0"),
        FILE("1"),
        OBJECT("2");

        final String value;

        DataType(String value) {
            this.value = value;
        }
    }

    public void setData(Serializable data) {
        if (this.dataType != DataType.OBJECT) {
            throw new IllegalArgumentException("Invalid data type");
        }
        this.data = data;
    }

    public void setDataLocation(@NonNull File dataLocation) {
        if (this.dataType != DataType.FILE) {
            throw new IllegalArgumentException("Invalid data type");
        }
        if (!dataLocation.isFile()) {
            throw new IllegalArgumentException("Invalid data location");
        }
        this.dataLocation = dataLocation;
    }

    protected static long timestamp() {
        return System.currentTimeMillis();
    }
}
