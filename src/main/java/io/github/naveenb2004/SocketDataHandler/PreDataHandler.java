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


public class PreDataHandler {
    public enum Method {
        SEND,
        RECEIVE
    }

    private final SocketDataHandler socketDataHandler;
    private final long UUID;
    private final String request;
    private final Method method;
    private final DataHandler.DataType dataType;
    private long totalDataSize;
    private long transferredDataSize;
    private boolean isCompleted;

    public PreDataHandler(SocketDataHandler socketDataHandler, long UUID, String request, Method method,
                          DataHandler.DataType dataType) {
        this.socketDataHandler = socketDataHandler;
        this.UUID = UUID;
        this.request = request;
        this.method = method;
        this.dataType = dataType;

        socketDataHandler.onPreUpdateReceived(this);
    }

    public long getUUID() {
        return UUID;
    }

    public String getRequest() {
        return request;
    }

    public Method getMethod() {
        return method;
    }

    public DataHandler.DataType getDataType() {
        return dataType;
    }

    public long getTotalDataSize() {
        return totalDataSize;
    }

    public long getTransferredDataSize() {
        return transferredDataSize;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setTransferredDataSize(long transferredDataSize) {
        this.transferredDataSize = transferredDataSize;
        socketDataHandler.onPreUpdateReceived(this);
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
        socketDataHandler.onPreUpdateReceived(this);
    }

    public void setTotalDataSize(long dataSize) {
        this.totalDataSize = dataSize;
    }
}
