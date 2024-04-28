package com.naveenb2004;

import com.naveenb2004.DataHandler.DataType;
import com.naveenb2004.PreUpdateHandler.PreDataHandler;
import com.naveenb2004.PreUpdateHandler.PreUpdateHandler;
import lombok.*;

import java.io.*;
import java.net.Socket;

public abstract class SocketDataHandler implements Closeable {
    @Getter
    @NonNull
    private final Socket SOCKET;
    private final Thread t;
    @Getter
    private static long defaultBufferSize = 1024L;
    @Getter
    @Setter
    @NonNull
    private static File tempFolder = new File("Temp");
    @Getter
    private final PreUpdateHandler preUpdateHandler = new PreUpdateHandler();
    public String id;

    public SocketDataHandler(@NonNull Socket SOCKET) {
        this.SOCKET = SOCKET;
        t = new Thread(new DataProcessor(this));
        t.start();
    }

    public static void setDefaultBufferSize(long defaultBufferSize) {
        if (defaultBufferSize <= 0) {
            throw new IllegalArgumentException("Default buffer size must be greater than 0 bytes!");
        }
        SocketDataHandler.defaultBufferSize = defaultBufferSize;
    }

    @Synchronized
    public void send(@NonNull DataHandler dataHandler) throws IOException {
        OutputStream os = SOCKET.getOutputStream();
        byte[] buffer = new byte[Math.toIntExact(defaultBufferSize)];
        PreDataHandler preDataHandler = new PreDataHandler(preUpdateHandler, dataHandler.getUUID(),
                dataHandler.getRequest(), PreDataHandler.Method.SEND, dataHandler.getDataType());
        byte[] data = DataProcessor.serialize(dataHandler, preDataHandler);

        os.write(data);
        os.flush();

        if (dataHandler.getDataType() != DataType.NONE) {
            FileInputStream fin = null;
            BufferedInputStream bos;
            if (dataHandler.getDataType() == DataType.FILE) {
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

    public abstract void onUpdateReceived(@NonNull DataHandler update);

    public void close() throws IOException {
        System.out.println("Lib : Close called by id = " + id);
        if (!SOCKET.isClosed()) {
            SOCKET.close();
            System.out.println("Lib : Socket closed by id = " + id);
        }
        if (t.isAlive()) {
            t.interrupt();
            System.out.println("Lib : Socket thread interrupted by id = " + id);
        }
    }
}
