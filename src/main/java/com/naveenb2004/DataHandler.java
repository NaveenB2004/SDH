package com.naveenb2004;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@RequiredArgsConstructor
public class DataHandler {
    @NonNull
    private final String request;
    @Setter(AccessLevel.PROTECTED)
    private String timestamp;
    private DataType dataType = DataType.NONE;
    private Serializable data;

    public enum DataType {
        NONE("0"),
        FILE("1"),
        OBJECT("2");

        final String value;

        DataType(String value) {
            this.value = value;
        }
    }

    public void setDataType(@NonNull DataType dataType) {
        this.dataType = dataType;
    }

    protected static String timestamp() {
        return null;
    }
}
