package com.naveenb2004;

import lombok.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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
    private static int maxDataChunk = 5 * 1024 * 1024;

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
        header.append(dataHandler.getTimestamp().getBytes(StandardCharsets.UTF_8).length).append(",");
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
            header.append(((File) dataHandler.getData()).length());
        }
        header.append("}");
        header.append(dataHandler.getRequest()).append(dataHandler.getTimestamp())
                .append(dataHandler.getDataType().value);

        out = new ByteArrayOutputStream();
        out.write(header.toString().getBytes(StandardCharsets.UTF_8));

        return out.toByteArray();
    }

    @Override
    public void run() {
        try {
            InputStream in = SOCKET.getInputStream();
            while (true) {
                if (in.read() == '{') {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] b = new byte[1];
                    while (in.read(b) != '}') {
                        out.write(b);
                    }

                    String[] headers = out.toString(StandardCharsets.UTF_8).split(",");

                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() throws IOException {
        if (SOCKET.isConnected()) {
            SOCKET.close();
        }
    }
}
