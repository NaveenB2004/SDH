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

import java.io.*;
import java.net.Socket;

public abstract class SocketDataHandler implements AutoCloseable {
    private final Socket SOCKET;
    private static int defaultBufferSize = 1024;
    private static File tempFolder = new File("Temp");

    /**
     * Bind connected socket with the library.
     * @param socket Connected socket connection
     */
    public SocketDataHandler(Socket socket) {
        if (socket == null) {
            throw new IllegalArgumentException("Socket can't be null!");
        }

        this.SOCKET = socket;
        final Thread DATA_PROCESSOR = new DataProcessor(this);
        DATA_PROCESSOR.setName("SocketDataHandler - DataProcessor");
        DATA_PROCESSOR.start();
    }

    /**
     * Get the bound socket.
     * @return Bound socket connection
     */
    public Socket getSocket() {
        return this.SOCKET;
    }

    /**
     * Set the default maximum buffer size for IO. This will help you when it comes to memory management while sending
     * and receiving data. Buffer size is saved in <code>bytes</code>.
     * @param defaultBufferSize Maximum buffer size
     */
    public static void setDefaultBufferSize(int defaultBufferSize) {
        if (defaultBufferSize <= 0) {
            throw new IllegalArgumentException("Default buffer size must be greater than 0 bytes!");
        }
        SocketDataHandler.defaultBufferSize = defaultBufferSize;
    }

    /**
     * Get the default maximum buffer size for IO.
     * @return Default maximum buffer size
     */
    public static int getDefaultBufferSize() {
        return defaultBufferSize;
    }

    /**
     * Set the default temporary folder for receive files. If this not set manually, library will automatically create
     * and use folder named 'Temp' in working directory. You need to consider file permissions when setting or using
     * default locations. Library will try to create folder if it does not exist.
     * @param folder Default temporary folder
     */
    public static void setTempFolder(File folder) {
        if (tempFolder == null) {
            throw new IllegalArgumentException("Temporary folder can't be null!");
        }
        tempFolder = folder;
    }

    /**
     * Get the default temporary folder that user specified or library specified for receive files.
     * @return Default temporary folder
     */
    public static File getTempFolder() {
        return tempFolder;
    }

    /**
     * Send data to the other end of the socket. If you're using multithreaded application, feel free to use this
     * method as you please. This method is synchronized for that kind of uses.
     * @param dataHandler Bundle of data need to be sent
     * @throws IOException              Data processing error
     * @throws IllegalArgumentException DataHandler is null
     */
    public synchronized void send(DataHandler dataHandler) throws IOException, IllegalArgumentException {
        if (dataHandler == null) {
            throw new IllegalArgumentException("DataHandler can't be null!");
        }

        OutputStream os = SOCKET.getOutputStream();
        byte[] buffer = new byte[defaultBufferSize];
        PreDataHandler preDataHandler = null;
        if (dataHandler.getDataType() != DataHandler.DataType.NONE) {
            preDataHandler = new PreDataHandler(this, dataHandler.getUUID(),
                    dataHandler.getRequest(), PreDataHandler.Method.SEND, dataHandler.getDataType());
        }
        byte[] data = DataProcessor.serialize(dataHandler, preDataHandler);

        os.write(data);
        os.flush();

        if (dataHandler.getDataType() != DataHandler.DataType.NONE) {
            FileInputStream fin = null;
            BufferedInputStream bos;
            if (dataHandler.getDataType() == DataHandler.DataType.FILE) {
                fin = new FileInputStream(dataHandler.getFile());
                bos = new BufferedInputStream(fin);
            } else {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(dataHandler.getData());
                oos.flush();
                oos.close();

                bos = new BufferedInputStream(new ByteArrayInputStream(baos.toByteArray()));
            }

            int c;
            int i = 0;
            while ((c = bos.read(buffer)) != -1) {
                os.write(buffer, 0, c);
                i += c;
                preDataHandler.setTransferredDataSize(i);
            }
            os.flush();
            bos.close();

            if (fin != null) {
                fin.close();
            }
            preDataHandler.setCompleted();
        }
    }

    /**
     * On an update from the other end of the socket <b>completely consumed and processed</b> by the library, this
     * method will call.
     * @param update Bundle of data received
     * @see DataHandler
     */
    public abstract void onUpdateReceived(DataHandler update);

    /**
     * On an update from the either this end or other end of the socket <b>consuming and processing</b> by the library,
     * this method will call. Only <code>serializable objects</code> and <code>files</code> updates can be retrieved
     * using this. Assume that you sent a file that has size of 10KB and default buffer size is 1KB. Then, when it comes
     * to sending/receiving, you will receive 11 updates via this method. After every attempt of send/receive data
     * buffer, this method will invoke. The last update will indicate that the process of sending/receiving is completed.
     * @param preUpdate Details of the pre-update
     * @see PreDataHandler
     */
    public abstract void onPreUpdateReceived(PreDataHandler preUpdate);

    /**
     * On disconnect of the either this end or the other end of the socket, this method will invoke.
     * @param status    Cause of the disconnect
     * @param exception Thrown exception, if any
     * @see io.github.naveenb2004.SocketDataHandler.DataProcessor.DisconnectStatus
     */
    public abstract void onDisconnected(DataProcessor.DisconnectStatus status, Exception exception);

    /**
     * Close the socket. You can directly use socket close without this.
     * @throws IOException Error while closing the connection
     */
    public void close() throws IOException {
        if (!SOCKET.isClosed()) {
            SOCKET.close();
        }
    }
}
