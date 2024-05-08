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

import io.github.naveenb2004.SocketDataHandler.DataHandler.DataType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataProcessor extends Thread {
    public enum DisconnectStatus {
        SOCKET_CLOSED,
        SOCKET_CLOSED_SELF,
        OTHER_IO_EXCEPTION_CAUGHT,
        INVALID_HEADER_DETECTED,
        INVALID_OBJECT_RECEIVED
    }

    protected static byte[] serialize(DataHandler dataHandler, PreDataHandler preDataHandler) throws IOException {
        ByteArrayOutputStream out;

        dataHandler.setTimestamp(DataHandler.timestamp());

        StringBuilder header = new StringBuilder("{");
        header.append(dataHandler.getRequest().getBytes(StandardCharsets.UTF_8).length).append(",");
        header.append(dataHandler.getDataType().getValue()).append(",");

        String suffix = "";
        if (dataHandler.getDataType() == DataType.OBJECT) {
            out = new ByteArrayOutputStream();

            ObjectOutputStream oOut = new ObjectOutputStream(out);
            oOut.writeObject(dataHandler.getData());
            oOut.flush();
            oOut.close();

            long length = out.toByteArray().length;
            header.append(length);
            preDataHandler.setTotalDataSize(length);
        } else if (dataHandler.getDataType() == DataHandler.DataType.FILE) {
            File file = dataHandler.getFile();
            suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1) + "$";
            long length = file.length();
            header.append(length);
            preDataHandler.setTotalDataSize(length);
        } else {
            header.append("0");
        }
        header.append("}");
        header.append(dataHandler.getRequest()).append(dataHandler.getTimestamp());
        header.append(suffix);

        out = new ByteArrayOutputStream();
        out.write(header.toString().getBytes(StandardCharsets.UTF_8));
        return out.toByteArray();
    }


    private final SocketDataHandler SOCKET_DATA_HANDLER;

    protected DataProcessor(SocketDataHandler socketDataHandler) {
        this.SOCKET_DATA_HANDLER = socketDataHandler;
    }

    @Override
    public void run() {
        try {
            InputStream in = SOCKET_DATA_HANDLER.getSocket().getInputStream();
            ByteArrayOutputStream out;
            byte[] buff;
            while (true) {
                int validator = in.read();
                if (validator == '{') {
                    int defaultBufferSize = SocketDataHandler.getDefaultBufferSize();
                    File tempFolder = SocketDataHandler.getTempFolder();

                    out = new ByteArrayOutputStream();
                    buff = new byte[1];
                    while (true) {
                        in.read(buff);

                        if (new String(buff, StandardCharsets.UTF_8).equals("}")) {
                            break;
                        }

                        out.write(buff);
                    }

                    String[] headers = out.toString(StandardCharsets.UTF_8).split(",");
                    int requestLen = Integer.parseInt(headers[0]);
                    int timestampLen = 13;
                    String dataTypeVal = headers[1];
                    long dataLen = Long.parseLong(headers[2]);

                    buff = new byte[requestLen];
                    in.read(buff);
                    String request = new String(buff, StandardCharsets.UTF_8);

                    buff = new byte[timestampLen];
                    in.read(buff);
                    long timestamp = Long.parseLong(new String(buff, StandardCharsets.UTF_8));

                    DataHandler dh = new DataHandler(request);
                    DataHandler.DataType dataType = DataHandler.DataType.getType(dataTypeVal);
                    PreDataHandler pdh = null;
                    if (dataType != DataType.NONE) {
                        pdh = new PreDataHandler(SOCKET_DATA_HANDLER, dh.getUUID(), request,
                                PreDataHandler.Method.RECEIVE, dataType);
                        pdh.setTotalDataSize(dataLen);
                    }

                    dh.setDataType(dataType);
                    dh.setTimestamp(timestamp);

                    out = new ByteArrayOutputStream();

                    if (dataType != DataType.NONE) {
                        long i = 0L;
                        if (dataType == DataType.OBJECT) {
                            buff = new byte[defaultBufferSize];
                            while (true) {
                                if (dataLen <= defaultBufferSize) {
                                    buff = new byte[Math.toIntExact(dataLen)];
                                    int c = in.read(buff);
                                    out.write(buff, 0, c);
                                    i += c;
                                    pdh.setTransferredDataSize(i);
                                    break;
                                } else {
                                    int c = in.read(buff);
                                    out.write(buff, 0, c);
                                    i += c;
                                    pdh.setTransferredDataSize(i);
                                }
                                dataLen -= defaultBufferSize;
                            }
                            ByteArrayInputStream bais = new ByteArrayInputStream(out.toByteArray());
                            ObjectInputStream ois = new ObjectInputStream(bais);

                            dh.setData((Serializable) ois.readObject());

                            ois.close();
                            bais.close();
                        } else if (dataType == DataType.FILE) {
                            buff = new byte[1];
                            while (true) {
                                in.read(buff);

                                if (new String(buff, StandardCharsets.UTF_8).equals("$")) {
                                    break;
                                }

                                out.write(buff);
                            }

                            if (!tempFolder.exists()) {
                                tempFolder.mkdirs();
                            }
                            Path tempFile = Files.createTempFile(tempFolder.toPath(), null,
                                    "." + out.toString(StandardCharsets.UTF_8));
                            FileOutputStream fos = new FileOutputStream(tempFile.toFile());
                            BufferedOutputStream bos = new BufferedOutputStream(fos);
                            buff = new byte[defaultBufferSize];
                            while (true) {
                                if (dataLen <= defaultBufferSize) {
                                    buff = new byte[Math.toIntExact(dataLen)];
                                    int c = in.read(buff);
                                    bos.write(buff, 0, c);
                                    i += c;
                                    pdh.setTransferredDataSize(i);
                                    break;
                                } else {
                                    int c = in.read(buff);
                                    bos.write(buff, 0, c);
                                    i += c;
                                    pdh.setTransferredDataSize(i);
                                }
                                dataLen -= defaultBufferSize;
                            }
                            bos.close();
                            fos.close();

                            dh.setFile(tempFile.toFile());
                        }

                        pdh.setCompleted(true);
                    }

                    SOCKET_DATA_HANDLER.onUpdateReceived(dh);
                } else {
                    SOCKET_DATA_HANDLER.close();

                    if (validator == -1) {
                        SOCKET_DATA_HANDLER.onDisconnected(DisconnectStatus.SOCKET_CLOSED, null);
                        break;
                    } else {
                        SOCKET_DATA_HANDLER.onDisconnected(DisconnectStatus.INVALID_HEADER_DETECTED, null);
                    }
                }
            }
        } catch (IOException e) {
            if (e.getMessage().equals("Socket closed")) {
                SOCKET_DATA_HANDLER.onDisconnected(DisconnectStatus.SOCKET_CLOSED_SELF, e);
            } else {
                SOCKET_DATA_HANDLER.onDisconnected(DisconnectStatus.OTHER_IO_EXCEPTION_CAUGHT, e);
            }
        } catch (ClassNotFoundException e) {
            SOCKET_DATA_HANDLER.onDisconnected(DisconnectStatus.INVALID_OBJECT_RECEIVED, e);
        }
    }
}
