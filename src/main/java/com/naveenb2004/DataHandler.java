package com.naveenb2004;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DataHandler {
    public enum DataType {
        NULL("0"),
        TEXT("1"),
        FILE("2"),
        RAW_BYTES("3"),
        OBJECT("4");

        final String typeId;

        DataType(String typeId) {
            this.typeId = typeId;
        }
    }

    @NonNull
    private String title;
    @NonNull
    private String timestamp; // LocalDateTime format (2024-04-18T07:52:30.328585500)
    @NonNull
    private DataType dataType;
    private byte[] data;

    public static DataHandlerBuilder builder() {
        return new DataHandlerBuilder();
    }

    public static class DataHandlerBuilder {
        private String title;
        private DataType dataType;
        private byte[] data;

        public DataHandlerBuilder title(String title) {
            this.title = title;
            return this;
        }

        public DataHandlerBuilder dataType(DataType dataType) {
            this.dataType = dataType;
            return this;
        }

        public DataHandlerBuilder data(byte[] data) {
            this.data = data;
            return this;
        }

        public DataHandler build() {
            String timestamp = "";
            return new DataHandler(title, timestamp, dataType, data);
        }
    }
}
