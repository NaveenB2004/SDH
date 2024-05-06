# Developer guids for Socket Data Handler

My recommendation is read this after you go through the [User guids for Socket Data Handler](../User/README.md).

- [Developer guids for Socket Data Handler](#developer-guids-for-socket-data-handler)
    - [Data Types](#data-types)
    - [The `DataHandler`](#the-datahandler)
    - [The `header`](#the-header)
    - [The `timestamp`](#the-timestamp)
    - [Appending the content](#appending-the-content)

## Data types

`Socket Data Handler` is using 3 data types for communications.

- NONE (just a `String` request of the `header`) [type `0`]
- FILE (`java.io.File`) [type `1`]
- OBJECT (any `serializable` object) [type `2`]

## The `DataHandler`

You must need a instance of `DataHandler` for send and receive data using this library. It's only bound to this library,
and it's **not a must** for the protocol. For more info. please look at the `Java-Doc`.

## The `header`

`Socket Data Handler` using a unique protocol for communications. If you are going to contribute or make your own
library for this, you should know how it works.

The main part of this protocol is the `header`. In the library,
the [`DataProcessor`](../../src/main/java/io/github/naveenb2004/SocketDataHandler/DataProcessor.java) is the core for
making that `header` (the `serialize` method). All `header`s are `UTF-8 String`s.

> Sample header

```json
{6,0,0}
```

`header` is only containing 3 elements enclosed by `{}` and seperated by `,`.

- The first element is the `bytes length of the request (UTF-8 String)`.
- The second element is the `data type`
- The third element is the `bytes length of the body`

Let's assume that we want to send a `File` of `2KB` with the `request`, `/samleFile`. Then the `header` is like this.

```json
{10,1,2048}
```

Now the library knows the bounds of the **variables**!

## The `timestamp`

All invocation of the `send` method is making a header (please look at [The header](#the-header) section for more about
the `header`s). The `timestamp` is part of the request content, but not showed-up in `header`. Timestamp is using
the [`Java` system milliseconds](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/System.html#currentTimeMillis()).
If you're going to use `ping` requests, please take a look at that. But why it's not in `header`? It's because
the `timestamp` always has **13 bytes**. It can be hardcoded!

## Appending the content

- Append the `header`
- Append the `request`
- Append the `timestamp`
- If it's a `File`, append the `file extension` and `$` (eg: myImage.png -> `png$`)
- If there's a body, append it