/*
 * Copyright 2024 Naveen Balasooriya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.naveenb2004.SocketDataHandler;

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
        UUID = getUUID();
    }

    public DataHandler(@NonNull String request,
                       @NonNull Serializable data) {
        this.request = request;
        this.dataType = DataType.OBJECT;
        this.data = data;
        UUID = getUUID();
    }

    public DataHandler(@NonNull String request,
                       @NonNull File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("Invalid file!");
        }
        this.request = request;
        this.dataType = DataType.FILE;
        this.file = file;
        UUID = getUUID();
    }

    private static long getUUID() {
        if (UUIDGenerator == (2 ^ 63 - 1)) {
            UUIDGenerator = 0L;
        }
        return UUIDGenerator++;
    }

    protected static long timestamp() {
        return System.currentTimeMillis();
    }
}
