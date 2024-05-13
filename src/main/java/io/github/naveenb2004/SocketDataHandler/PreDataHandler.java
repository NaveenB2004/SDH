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
    /**
     * Pre update method. It means that the onPreUpdateReceived() can be triggered either sending or receiving data.
     */
    public enum Method {
        SEND,
        RECEIVE
    }

    private final SocketDataHandler SOCKET_DATA_HANDLER;
    private final long UUID;
    private final String REQUEST;
    private final Method METHOD;
    private final DataHandler.DataType DATA_TYPE;
    private long totalDataSize;
    private long transferredDataSize;
    private boolean isCompleted;

    /**
     * Handler of the pre-updates. This is only for in-library use.
     * @param socketDataHandler Call the onPreUpdateReceived()
     * @param UUID              Share with the completed DataHandler
     * @param request           Request string
     * @param method            Send/Receive
     * @param dataType          Data type
     */
    protected PreDataHandler(SocketDataHandler socketDataHandler, long UUID, String request, Method method,
                             DataHandler.DataType dataType) {
        this.SOCKET_DATA_HANDLER = socketDataHandler;
        this.UUID = UUID;
        this.REQUEST = request;
        this.METHOD = method;
        this.DATA_TYPE = dataType;

        socketDataHandler.onPreUpdateReceived(this);
    }

    /**
     * Get the UUID shared with DataHandler.
     * @return UUID
     * @see DataHandler
     */
    public long getUUID() {
        return UUID;
    }

    /**
     * Get the request string.
     * @return Request string
     */
    public String getRequest() {
        return REQUEST;
    }

    /**
     * Get the method (send/receive)
     * @return Method
     * @see Method
     */
    public Method getMethod() {
        return METHOD;
    }

    /**
     * Get the data type
     * @return Data type
     * @see DataHandler.DataType
     */
    public DataHandler.DataType getDataType() {
        return DATA_TYPE;
    }

    /**
     * Get the total data size of the send/received update.
     * @return Total data size in <code>bytes</code>
     */
    public long getTotalDataSize() {
        return totalDataSize;
    }

    /**
     * Get the transferred data size of the send/received update.
     * @return Transferred data size in <code>bytes</code>
     */
    public long getTransferredDataSize() {
        return transferredDataSize;
    }

    /**
     * Get the complete status of the update.
     * @return Complete status
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Set transferred data size. This method is only for in-library use.
     * @param transferredDataSize Transferred data size in <code>bytes</code>
     */
    protected void setTransferredDataSize(long transferredDataSize) {
        this.transferredDataSize = transferredDataSize;
        SOCKET_DATA_HANDLER.onPreUpdateReceived(this);
    }

    /**
     * Set complete status. This method is only for in-library use.
     */
    protected void setCompleted() {
        this.isCompleted = true;
        SOCKET_DATA_HANDLER.onPreUpdateReceived(this);
    }

    /**
     * Set total data size. This method is only for in-library use.
     * @param dataSize Total data size in <code>bytes</code>
     */
    protected void setTotalDataSize(long dataSize) {
        this.totalDataSize = dataSize;
    }
}
