package com.naveenb2004;

import lombok.*;

import java.io.*;
import java.net.Socket;

@RequiredArgsConstructor
public abstract class SocketDataHandler implements Runnable, AutoCloseable {
    @Getter
    @NonNull
    private final Socket SOCKET;
    @Getter
    protected static long defaultBufferSize = 1024L;
    @Getter
    @Setter
    @NonNull
    protected static File tempFolder = new File("Temp");

    public static void setDefaultBufferSize(long defaultBufferSize) {
        if (defaultBufferSize <= 0) {
            throw new IllegalArgumentException("Default buffer size must be greater than 0 bytes!");
        }
        SocketDataHandler.defaultBufferSize = defaultBufferSize;
    }

    public abstract void receive(@NonNull DataHandler update);

    @Synchronized
    public void send(@NonNull DataHandler dataHandler) throws IOException {
        OutputStream os = SOCKET.getOutputStream();
        byte[] buffer = new byte[Math.toIntExact(defaultBufferSize)];
        byte[] data = DataProcessor.serialize(dataHandler);

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
                @Cleanup
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(dataHandler.getData());
                oos.flush();
                bos = new BufferedInputStream(new ByteArrayInputStream(baos.toByteArray()));
            }

            int c;
            int i = 0;
            while ((c = bos.read(buffer)) != -1) {
                os.write(buffer, 0, c);
                i += c;
                dataHandler.setTransferredDataSize(i);
            }
            os.flush();
            bos.close();

            if (fin != null) {
                fin.close();
            }
        }
    }

    @SneakyThrows
    @Override
    public void run() {
        new DataProcessor().deserialize(this);
    }

    public void close() throws IOException {
        if (SOCKET.isConnected()) {
            SOCKET.close();
        }
    }
}
