package com.naveenb2004;

import lombok.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor
public abstract class SocketDataHandler implements Runnable, AutoCloseable {
    @Getter
    @NonNull
    private final Socket SOCKET;
    @Getter
    @Setter
    private static int defaultBufferSize = 1024;
    @Getter
    @Setter
    private static File tempFolder = new File("Temp");

    public abstract void receive(@NonNull DataHandler update);

    @Synchronized
    public void send(@NonNull DataHandler dataHandler) throws IOException {
        OutputStream os = SOCKET.getOutputStream();
        byte[] buffer = new byte[defaultBufferSize];
        byte[] data = serialize(dataHandler);

        os.write(data);
        os.flush();

        if (dataHandler.getDataType() != DataHandler.DataType.NONE) {
            BufferedInputStream bos = null;
            if (dataHandler.getDataType() == DataHandler.DataType.OBJECT) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                @Cleanup
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(dataHandler.getData());
                oos.flush();
                bos = new BufferedInputStream(new ByteArrayInputStream(baos.toByteArray()));
            } else if (dataHandler.getDataType() == DataHandler.DataType.FILE) {
                @Cleanup
                FileInputStream fin = new FileInputStream((File) dataHandler.getData());
                bos = new BufferedInputStream(fin);
            }

            int c;
            while ((c = bos.read(buffer)) != -1) {
                os.write(buffer, 0, c);
            }
            os.flush();
            bos.close();
        }
    }

    @NonNull
    protected static byte[] serialize(@NonNull DataHandler dataHandler) throws IOException {
        ByteArrayOutputStream out;

        dataHandler.setTimestamp(DataHandler.timestamp());

        StringBuilder header = new StringBuilder("{");
        header.append(dataHandler.getRequest().getBytes(StandardCharsets.UTF_8).length).append(",");
        header.append(dataHandler.getDataType().value.getBytes(StandardCharsets.UTF_8).length).append(",");

        if (dataHandler.getDataType() == DataHandler.DataType.NONE) {
            header.append("0");
        } else if (dataHandler.getDataType() == DataHandler.DataType.OBJECT) {
            out = new ByteArrayOutputStream();
            @Cleanup
            ObjectOutputStream oOut = new ObjectOutputStream(out);
            oOut.writeObject(dataHandler.getData());
            oOut.flush();
            header.append(out.toByteArray().length);
        } else if (dataHandler.getDataType() == DataHandler.DataType.FILE) {
            File file = (File) dataHandler.getData();
            String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            header.append(suffix).append("$").append(file.length());
        }
        header.append("}");
        header.append(dataHandler.getRequest()).append(dataHandler.getTimestamp())
                .append(dataHandler.getDataType().value);

        out = new ByteArrayOutputStream();
        out.write(header.toString().getBytes(StandardCharsets.UTF_8));

        return out.toByteArray();
    }

    @SneakyThrows
    @Override
    public void run() {
        InputStream in = SOCKET.getInputStream();
        ByteArrayOutputStream out;
        byte[] buff;
        while (true) {
            if (in.read() == '{') {
                out = new ByteArrayOutputStream();
                buff = new byte[1];
                while (in.read(buff) != '}') {
                    out.write(buff);
                }

                String[] headers = out.toString(StandardCharsets.UTF_8).split(",");
                int requestLen = Integer.parseInt(headers[0]);
                int timestampLen = 13;
                String dataTypeVal = headers[2];
                int dataLen = Integer.parseInt(headers[3]);

                buff = new byte[requestLen];
                in.read(buff);
                String request = new String(buff, StandardCharsets.UTF_8);

                buff = new byte[timestampLen];
                in.read(buff);
                long timestamp = Long.parseLong(new String(buff, StandardCharsets.UTF_8));

                DataHandler.DataType dataType = DataHandler.DataType.valueOf(dataTypeVal);

                DataHandler dh = new DataHandler(request, dataType);
                dh.setTimestamp(timestamp);

                out = new ByteArrayOutputStream();
                if (dataType == DataHandler.DataType.OBJECT) {
                    buff = new byte[defaultBufferSize];
                    while (true) {
                        if (dataLen <= defaultBufferSize) {
                            buff = new byte[dataLen];
                            in.read(buff);
                            out.write(buff);
                            break;
                        } else {
                            in.read(buff);
                            out.write(buff);
                        }
                        dataLen -= defaultBufferSize;
                    }
                    dh.setData(out.toByteArray());
                } else if (dataType == DataHandler.DataType.FILE) {
                    buff = new byte[1];
                    while (in.read(buff) != '$') {
                        out.write(buff);
                    }

                    Path tempFile = Files.createTempFile(tempFolder.toPath(), null,
                            out.toString(StandardCharsets.UTF_8));
                    @Cleanup
                    FileOutputStream fos = new FileOutputStream(tempFolder);
                    @Cleanup
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    while (true) {
                        if (dataLen <= defaultBufferSize) {
                            buff = new byte[dataLen];
                            in.read(buff);
                            bos.write(buff);
                            break;
                        } else {
                            in.read(buff);
                            bos.write(buff);
                        }
                        dataLen -= defaultBufferSize;
                    }
                    dh.setDataLocation(tempFile.toFile());
                }

                receive(dh);
            }
        }
    }

    public void close() throws IOException {
        if (SOCKET.isConnected()) {
            SOCKET.close();
        }
    }
}
