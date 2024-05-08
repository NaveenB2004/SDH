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

public abstract class SocketDataHandler implements Closeable {
    private final Socket SOCKET;
    private static int defaultBufferSize = 1024;
    private static File tempFolder = new File("Temp");

    public SocketDataHandler(Socket SOCKET) {
        if (SOCKET == null) {
            throw new IllegalArgumentException("Socket can't be null!");
        }

        this.SOCKET = SOCKET;
        final Thread DATA_PROCESSOR = new DataProcessor(this);
        DATA_PROCESSOR.setName("SocketDataHandler - DataProcessor");
        DATA_PROCESSOR.start();
    }

    public Socket getSocket() {
        return this.SOCKET;
    }

    public static void setDefaultBufferSize(int defaultBufferSize) {
        if (defaultBufferSize <= 0) {
            throw new IllegalArgumentException("Default buffer size must be greater than 0 bytes!");
        }
        SocketDataHandler.defaultBufferSize = defaultBufferSize;
    }

    public static int getDefaultBufferSize() {
        return defaultBufferSize;
    }

    public static void setTempFolder(File folder) {
        if (tempFolder == null) {
            throw new IllegalArgumentException("Temporary folder can't be null!");
        }
        tempFolder = folder;
    }

    public static File getTempFolder() {
        return tempFolder;
    }

    public synchronized void send(DataHandler dataHandler) throws IOException {
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
            preDataHandler.setCompleted(true);
        }
    }

    public abstract void onUpdateReceived(DataHandler update);

    public abstract void onPreUpdateReceived(PreDataHandler preUpdate);

    public abstract void onDisconnected(DataProcessor.DisconnectStatus status, Exception exception);

    public void close() throws IOException {
        if (!SOCKET.isClosed()) {
            SOCKET.close();
        }
    }
}
