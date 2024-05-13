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
    // handle UUIDs
    protected static long UUIDGenerator = 0L;

    private final long UUID;
    private final String REQUEST;
    private long timestamp;
    private DataType dataType;
    private Serializable data;
    private File file;

    /**
     * Automatically assigned data type for DataHandler.
     */
    public enum DataType {
        /**
         * No specific data. Just request.
         */
        NONE("0"),
        /**
         * java.io.File
         */
        FILE("1"),
        /**
         * Any <code>serializable</code> object
         */
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

    /**
     * Process the UUIDs for  DataHandler objects
     * @return UUID
     */
    private static long UUID() {
        if (UUIDGenerator == Long.MAX_VALUE) {
            UUIDGenerator = 0L;
        }
        return UUIDGenerator++;
    }

    /**
     * Get the current timestamp.
     * @return Current timestamp in milliseconds
     * @see System#currentTimeMillis()
     */
    protected static long timestamp() {
        return System.currentTimeMillis();
    }

    /**
     * Data bundle with only request.
     * @param request Request string
     */
    public DataHandler(String request) {
        if (request == null) {
            throw new IllegalArgumentException("Request can't be null!");
        }
        this.REQUEST = request;
        this.dataType = DataType.NONE;
        UUID = UUID();
    }

    /**
     * Data bundle with <code>Serializable</code> object.
     * @param request Request string
     * @param data    Serializable object
     * @see Serializable
     */
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

    /**
     * Data bundle with <code>File</code>
     * @param request Request string
     * @param file    java.io.File
     * @see java.io.File
     */
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

    /**
     * Get the UUID
     * @return UUID
     */
    public long getUUID() {
        return UUID;
    }

    /**
     * Get the request string
     * @return Request string
     */
    public String getRequest() {
        return REQUEST;
    }

    /**
     * Get the timestamp
     * @return Timestamp in milliseconds
     * @see System#currentTimeMillis()
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Set the timestamp. This method is only for in-library use.
     * @param timestamp Timestamp in milliseconds
     * @see System#currentTimeMillis()
     */
    protected void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Get the data type
     * @return Data type
     * @see DataType
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Set the data type. This method is only for in-library use.
     * @param dataType Data type
     * @see DataType
     */
    protected void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    /**
     * Get the serializable object (if any).
     * @return Serializable object
     * @see Serializable
     */
    public Serializable getData() {
        return data;
    }

    /**
     * Set the serializable object (if any). This method is only for in-library use.
     * @param data Serializable object
     * @see Serializable
     */
    protected void setData(Serializable data) {
        this.data = data;
    }

    /**
     * Get the <code>java.io.File</code> (if any).
     * @return <code>java.io.File</code>
     * @see java.io.File
     */
    public File getFile() {
        return file;
    }

    /**
     * Set the <code>java.io.File</code> (if any). This method is only for in-library use.
     * @param file <code>java.io.File</code>
     * @see java.io.File
     */
    protected void setFile(File file) {
        this.file = file;
    }
}
