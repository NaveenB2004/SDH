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

import java.io.File;
import java.io.Serializable;

public class DataHandler {
    protected static long UUIDGenerator = 0L;

    private final long UUID;
    private final String REQUEST;
    private long timestamp;
    private DataType dataType;
    private Serializable data;
    private File file;

    public enum DataType {
        NONE("0"),
        FILE("1"),
        OBJECT("2");

        private final String value;

        DataType(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }

        static DataType getType(String value) {
            switch (value) {
                case "0":
                    return NONE;
                case "1":
                    return FILE;
                case "2":
                    return OBJECT;
                default:
                    throw new IllegalArgumentException("Invalid value!");
            }
        }
    }

    private static long UUID() {
        if (UUIDGenerator == Long.MAX_VALUE) {
            UUIDGenerator = 0L;
        }
        return UUIDGenerator++;
    }

    protected static long timestamp() {
        return System.currentTimeMillis();
    }

    public DataHandler(String request) {
        if (request == null) {
            throw new IllegalArgumentException("Request can't be null!");
        }
        this.REQUEST = request;
        this.dataType = DataType.NONE;
        UUID = UUID();
    }

    public DataHandler(String request, Serializable data) {
        if (request == null) {
            throw new IllegalArgumentException("Request can't be null!");
        }
        if (data == null) {
            throw new IllegalArgumentException("Data can't be null!");
        }
        this.REQUEST = request;
        this.dataType = DataType.OBJECT;
        this.data = data;
        UUID = UUID();
    }

    public DataHandler(String request, File file) {
        if (request == null) {
            throw new IllegalArgumentException("Request can't be null!");
        }
        if (file == null) {
            throw new IllegalArgumentException("File can't be null!");
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("Invalid file!");
        }
        this.REQUEST = request;
        this.dataType = DataType.FILE;
        this.file = file;
        UUID = UUID();
    }

    public long getUUID() {
        return UUID;
    }

    public String getRequest() {
        return REQUEST;
    }

    public long getTimestamp() {
        return timestamp;
    }

    protected void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public DataType getDataType() {
        return dataType;
    }

    protected void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Serializable getData() {
        return data;
    }

    protected void setData(Serializable data) {
        this.data = data;
    }

    public File getFile() {
        return file;
    }

    protected void setFile(File file) {
        this.file = file;
    }
}
