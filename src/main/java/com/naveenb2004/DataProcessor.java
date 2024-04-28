package com.naveenb2004;

import com.naveenb2004.DataHandler.DataType;
import com.naveenb2004.PreUpdateHandler.PreDataHandler;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataProcessor extends Thread {
    @NonNull
    protected static byte[] serialize(@NonNull DataHandler dataHandler,
                                      PreDataHandler preDataHandler) throws IOException {
        ByteArrayOutputStream out;

        dataHandler.setTimestamp(DataHandler.timestamp());

        StringBuilder header = new StringBuilder("{");
        header.append(dataHandler.getRequest().getBytes(StandardCharsets.UTF_8).length).append(",");
        header.append(dataHandler.getDataType().getValue()).append(",");

        String suffix = "";
        if (dataHandler.getDataType() == DataType.OBJECT) {
            out = new ByteArrayOutputStream();
            @Cleanup
            ObjectOutputStream oOut = new ObjectOutputStream(out);
            oOut.writeObject(dataHandler.getData());
            oOut.flush();
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

    @NonNull
    private final SocketDataHandler SOCKET_DATA_HANDLER;

    protected DataProcessor(@NonNull SocketDataHandler socketDataHandler) {
        this.SOCKET_DATA_HANDLER = socketDataHandler;
    }

    @SneakyThrows
    @Override
    public void run() {
        InputStream in = SOCKET_DATA_HANDLER.getSOCKET().getInputStream();
        ByteArrayOutputStream out;
        byte[] buff;
        while (true) {
            if (in.read() == '{') {
                long defaultBufferSize = SocketDataHandler.getDefaultBufferSize();
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
                    pdh = new PreDataHandler(SOCKET_DATA_HANDLER.getPRE_UPDATE_HANDLER(),
                            dh.getUUID(), request, PreDataHandler.Method.RECEIVE, dataType);
                    pdh.setTotalDataSize(dataLen);
                }

                dh.setDataType(dataType);
                dh.setTimestamp(timestamp);

                out = new ByteArrayOutputStream();

                if (dataType != DataType.NONE) {
                    long i = 0L;
                    if (dataType == DataType.OBJECT) {
                        buff = new byte[Math.toIntExact(defaultBufferSize)];
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
                        @Cleanup
                        ByteArrayInputStream bais = new ByteArrayInputStream(out.toByteArray());
                        @Cleanup
                        ObjectInputStream ois = new ObjectInputStream(bais);

                        dh.setData((Serializable) ois.readObject());
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
                        buff = new byte[Math.toIntExact(defaultBufferSize)];
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
                break;
            }
        }

        SOCKET_DATA_HANDLER.close();
    }
}
