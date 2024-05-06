# User guids for Socket Data Handler

- [User guids for Socket Data Handler](#user-guids-for-socket-data-handler)
    - [Import into the project](#import-into-the-project)
    - [Common library settings](#common-library-settings)
    - [Setting up the receiver](#setting-up-the-receiver)
    - [Connect socket and library](#connect-socket-and-library)
    - [Receiving pre-updates](#receiving-pre-updates)
    - [Send data](#send-data)
    - [Close socket](#close-socket)
    - [Further Reading](#further-reading)

## Import into the project

[![Central Repository](https://img.shields.io/maven-central/v/io.github.naveenb2004/SocketDataHandler
)](https://central.sonatype.com/artifact/io.github.naveenb2004/SocketDataHandler)
[![Minimum JDK version](https://img.shields.io/badge/Minumum_JDK-v11-green)](#)\
If you are using project managers like `Maven`, `Gradle` etc., please take a look at
the [Central repository page for this project](https://central.sonatype.com/artifact/io.github.naveenb2004/SocketDataHandler).
You can find most of the import snippets for project managers from there. For `Maven`:

```xml

<dependency>
    <groupId>io.github.naveenb2004</groupId>
    <artifactId>SocketDataHandler</artifactId>
    <version><!-- set version --></version>
</dependency>
```

## Common library settings

In this library, you can set up maximum IO buffer size for data chunks (in `bytes`) and default location for work
with `Files`. To do that place this code before any initialization of the `SocketDataHandler`.

```java
// set default maximum buffer size for chunks
SocketDataHandler.setDefaultBufferSize(1024);

// set default temporary folder for files downloading
SocketDataHandler.setTempFolder(new File("Temp"));
```

## Setting up the receiver

Now you're good to code your receiver file. To do that, make a class that `extends` the `SocketDataHandler` and
implement the required constructors and methods in that class.

> MySocket.java

```java
public class MySocket extends SocketDataHandler {
    public MySocket(Socket SOCKET) {
        super(SOCKET);
    }

    @Override
    public void onUpdateReceived(DataHandler update) {
        
    }

    @Override
    public void onDisconnected(DataProcessor.DisconnectStatus status, Exception exception) {

    }
}
```

All completed updates are pushed to the `onUpdateReceived(DataHandler update)` method and if socket disconnected (either
end) it will push to `onDisconnected(DataProcessor.DisconnectStatus status, Exception exception)` method.

## Connect socket and library

`SocketDataHandler` is just a library to manage socket IO operations. So you need to work on your network operations.

```java
// connect to the server
Socket socket = new Socket("localhost", port);
            
// connect socket with library
MySocket clientSocket = new MySocket(socket);
```

## Receiving pre-updates

`Pre-update` means that uncompleted, processing (receiving or sending) update. You can only get pre-updates
for `Serializable Objects` and `Files`. To do that you need to implement a `pre-updates watcher` class and register it
with the `receiver` class that `extends` the `SocketDataHandler`.

> MyPreUpdateWatcher.java

```java
public class MyPreUpdateReceiver implements PreUpdateWatcher {
    @Override
    public void onPreUpdateSeen(PreDataHandler preUpdate) {

    }
}
```

> MySocket.java

```java
public class MySocket extends SocketDataHandler {
    public MySocket(Socket SOCKET) {
        super(SOCKET);
        // register the watcher
        PreUpdateHandler watcher = getPRE_UPDATE_HANDLER();
        watcher.addWatcher(new MyPreUpdateReceiver());
    }

    @Override
    public void onUpdateReceived(DataHandler update) {

    }

    @Override
    public void onDisconnected(DataProcessor.DisconnectStatus status, Exception exception) {

    }
}
```

## Send data

You can use `DataHandler` object to store your request and use `send` method to send it. Note that the `send` method is
part of your receiver class that `extends` with the `SocketDataHandler`.

```java
DataHandler dataHandler = new DataHandler("/SendText");
try {
    clientSocket.send(dataHandler);
} catch (IOException e) {
    throw new RuntimeException(e);
}
```

## Connection between `onUpdateReceived` and `preUpdateSeen`

When an update received to the `receiver`/`sender`, it automatically give a `UUID (Universally Unique Identifier)` for
that request/response. If your request/response is eligible for `pre-update` it will automatically share that `UUID`.

## Close socket

You can either direct close the socket or use `close` method of your receiver class that `extends` with
the `SocketDataHandler`. On successful close, you will receive an update for `onDisconneted`.

```java
try {
    clientSocket.close();
} catch (IOException e) {
    throw new RuntimeException(e);
}
```

## Further reading

Please take a look at the `Java-Doc` to get a clear idea about methods and interfaces. 