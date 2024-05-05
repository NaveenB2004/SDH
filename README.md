# Socket Data Handler

`Socket Data Handler` is basically a `communication protocol`, that written in `Java` language. As the basic meaning of
the `protocol`, this will help users to communicate between other socket connections with a specific method. This is not
bound to the `text` based communication. In core, it's all about `bytes`. But don't worry, you don't need to work
directly with bytes! Also, **if you need to use another programming language as the client or server, yes you can!** You
just need to follow the order of this `protocol` and implement in other language. Also note that, **this library is just
a `IO` tool and you still need to work on your network and data encryption parts.**

- [Socket Data Handler](#socket-data-handler)
    - [Why?](#why)
    - [Import? It's easy!](#import-its-easy)
    - [Usage](#usage)
    - [License](#license)

## Why?

Socket Data Handler is a fully managed communication library. It means, this library is helps you to low the pain of
using the default socket input and output mechanism. This library is working on the top of that default system, but
managed in every possible ways (that I can find and develop with my knowledge and experience XD). If you love to see
how, please take a look at the [developer docs](Docs/Dev/README.md).

## Import? It's easy!

![Central Repository](https://img.shields.io/maven-central/v/io.github.naveenb2004/SocketDataHandler
)\
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

## Usage

- Please refer the [user guids](Docs/User/README.md) to know how this library is going to make your works easy 😎.
- Please refer the [developer guids](Docs/Dev/README.md) to know how developers are going to smile (or maybe laugh at
  me) 😁.

## License

[Copyright 2024 Naveen Balasooriya](LICENSE)