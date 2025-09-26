# REPL Framework for Java

A lightweight and extensible REPL (Read-Eval-Print Loop) framework for building interactive CLI applications in Java.

## Features

* Command registration with single or multiple aliases
* Fluent Builder API for commands
* Typed argument parsing (supports `int`, `boolean`, `double`, `String`, etc.)
* Support for quoted and escaped arguments
* Asynchronous command execution support
* Result handlers for commands (sync and async)
* REPL lifecycle control: start, stop, and execute
* Minimal setup, easy integration into any Java application

---

## Getting Started

```java
public class Main {
    public static void main(String[] args) {
        ReplRunner repl = new ReplRunner();

        repl.getRegistry()
            // Register synchronous command
            .command("hello")
                .description("Prints a greeting")
                .action((r, a) -> {
                    r.println("Hello from the REPL!");
                    return 0;
                });

        repl.getRegistry()
            // Register command with typed arguments
            .command("add")
                .description("Adds two numbers")
                .action((r, a) -> {
                    int x = a.next(int.class);
                    int y = a.next(int.class);
                    r.println("Sum: " + (x + y));
                    return 0;
                });

        repl.getRegistry()
            // Register async command
            .command("wait")
                .description("Simulates async delay")
                .async()
                .action((r, a) -> {
                    Thread.sleep(1000);
                    return "Finished waiting";
                })
                .result((r, result) -> r.println(result));

        repl.getRegistry()
            // Register multi-alias exit command
            .command("exit", "e", "/q")
                .description("Stops the REPL")
                .action((r, a) -> {
                    r.stop();
                    return 0;
                });

        repl.start();
    }
}
```

---

## Argument Parsing Examples

### Basic

```java
Arguments args = Arguments.parse("--port 8080 --debug true");

int port = args.get(int.class, "--port");
boolean debug = args.get(boolean.class, "--debug");
```

### With Quoted Values

```java
Arguments args = Arguments.parse("send \"Hello world\" user42");

String message = args.next();  // Hello world
String user = args.next();     // user42
```

### Key-Value Syntax

```java
Arguments args = Arguments.parse("--mode=fast --retries=3");
String mode = args.get("--mode");
int retries = args.get(int.class, "--retries");
```

---

## Built-In Components

| Component                 | Description                                   |
| ------------------------- | --------------------------------------------- |
| `ReplRunner`              | Main REPL engine that controls execution      |
| `CommandRegistry`         | Manages command registration                  |
| `Command`                 | Represents a single command                   |
| `Arguments`               | Parses and converts user input into arguments |
| `ArgumentParseException`  | Thrown for invalid or missing arguments       |
| `UnknownCommandException` | Thrown for unrecognized commands              |

---

## Command Examples

### Echo Command

```java
repl.getRegistry().command("echo")
    .description("Echoes input")
    .action((r, a) -> {
        while (a.hasNext()) r.print(a.next() + " ");
        r.println();
        return 0;
    });
```

**Usage:**

```
echo Hello REPL Framework
```

**Output:**

```
Hello REPL Framework
```

---

### Flag Parsing

```java
repl.getRegistry().command("config")
    .description("Parses flags")
    .action((r, a) -> {
        if (a.contains("--debug")) r.println("Debug mode enabled");
        String mode = a.get("--mode", "default");
        r.println("Mode: " + mode);
        return 0;
    });
```

**Usage:**

```
config --debug --mode=production
```

**Output:**

```
Debug mode enabled
Mode: production
```

---

### Multiple Aliases

```java
repl.getRegistry().command("quit", "exit", "/q")
    .description("Stops the REPL")
    .action((r, a) -> {
        r.stop();
        return 0;
    });
```

---

### Argument Type Conversion

```java
repl.getRegistry().command("multiply")
    .description("Multiplies two integers")
    .action((r, a) -> {
        int x = a.next(int.class);
        int y = a.next(int.class);
        r.println("Result: " + (x * y));
        return 0;
    });
```

---

### Help Command with Prefix

```java
repl.getRegistry().command("help")
    .description("Shows all or filtered commands")
    .action((r, a) -> {
        String prefix = a.hasNext() ? a.next() : "";
        r.println(r.getRegistry().formattedCommandList(prefix));
        return 0;
    });
```

**Usage:**

```
help
help mu
```

---

## Async Command with Result Handler

```java
repl.setExecutor(executor);

repl.getRegistry().command("compute")
    .description("Performs async computation")
    .async()
    .action((r, a) -> {
        Thread.sleep(500);
        return 42;
    })
    .result((r, result) -> r.println("Computed: " + result));
```

---

## License

[Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0)
