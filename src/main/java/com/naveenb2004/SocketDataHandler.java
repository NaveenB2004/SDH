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
            BufferedInputStream bos;
            FileInputStream fin = null;
            if (dataHandler.getDataType() == DataHandler.DataType.FILE) {
                fin = new FileInputStream(dataHandler.getFile());
                bos = new BufferedInputStream(fin);
            } else {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                @Cleanup
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(dataHandler.getData());
                oos.flush();
                bos = new BufferedInputStream(new ByteArrayInputStream(baos.toByteArray()));
            }

            int c;
            while ((c = bos.read(buffer)) != -1) {
                os.write(buffer, 0, c);
            }
            os.flush();
            bos.close();

            if (fin != null) {
                fin.close();
            }
        }
    }

    @NonNull
    protected static byte[] serialize(@NonNull DataHandler dataHandler) throws IOException {
        ByteArrayOutputStream out;

        dataHandler.setTimestamp(DataHandler.timestamp());

        StringBuilder header = new StringBuilder("{");
        header.append(dataHandler.getRequest().getBytes(StandardCharsets.UTF_8).length).append(",");
        header.append(dataHandler.getDataType().getValue()).append(",");

        String suffix = "";
        if (dataHandler.getDataType() == DataHandler.DataType.OBJECT) {
            out = new ByteArrayOutputStream();
            @Cleanup
            ObjectOutputStream oOut = new ObjectOutputStream(out);
            oOut.writeObject(dataHandler.getData());
            oOut.flush();
            header.append(out.toByteArray().length);
        } else if (dataHandler.getDataType() == DataHandler.DataType.FILE) {
            File file = dataHandler.getFile();
            suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1) + "$";
            header.append(file.length());
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
                int dataLen = Integer.parseInt(headers[2]);

                buff = new byte[requestLen];
                in.read(buff);
                String request = new String(buff, StandardCharsets.UTF_8);

                buff = new byte[timestampLen];
                in.read(buff);
                long timestamp = Long.parseLong(new String(buff, StandardCharsets.UTF_8));

                DataHandler dh;
                DataHandler.DataType dataType = DataHandler.DataType.getType(dataTypeVal);
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
                    dh = new DataHandler(request, out.toByteArray());
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
                    dh = new DataHandler(request, tempFile.toFile());
                } else {
                    dh = new DataHandler(request);
                }

                dh.setDataType(dataType);
                dh.setTimestamp(timestamp);

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
