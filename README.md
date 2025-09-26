# REPL Framework for Java

A lightweight and extensible REPL (Read-Eval-Print Loop) framework for building interactive CLI applications in Java.

## Features

* Command registration with single or multiple aliases
* Typed argument parsing (supports `int`, `boolean`, `double`, `String`, etc.)
* Support for quoted and escaped arguments
* Asynchronous command execution support
* Result handlers for commands (sync and async)
* REPL lifecycle control: start, stop, and execute
* Minimal setup, easy integration into any Java application

---

## Getting Started

```java
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ReplRunner repl = new ReplRunner();

        repl.getRegistry()
            // Register synchronous command
            .register("hello", "Prints a greeting", (r, a) -> {
                r.println("Hello from the REPL!");
                return 0;
            })

            // Register command with typed arguments
            .register("add", "Adds two numbers", (r, a) -> {
                int x = a.next(int.class);
                int y = a.next(int.class);
                r.println("Sum: " + (x + y));
                return 0;
            })

            // Register async command
            .registerAsync("wait", "Simulates async delay", (r, a) -> {
                Thread.sleep(1000);
                return "Finished waiting";
            }, (r, result) -> r.println(result))

            // Register multi-alias exit command
            .register(List.of("exit", "e", "/q"), "Stops the REPL", (r, a) -> {
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
ArgumentParser parser = ArgumentParser.parse("--port 8080 --debug true");

int port = parser.get(int.class, "--port");
boolean debug = parser.get(boolean.class, "--debug");
```

### With Quoted Values

```java
ArgumentParser parser = ArgumentParser.parse("send \"Hello world\" user42");

String message = parser.next();  // Hello world
String user = parser.next();     // user42
```

### Key-Value Syntax

```java
ArgumentParser parser = ArgumentParser.parse("--mode=fast --retries=3");
String mode = parser.get("--mode");
int retries = parser.get(int.class, "--retries");
```

---

## Built-In Components

| Component                 | Description                                   |
| ------------------------- | --------------------------------------------- |
| `ReplRunner`              | Main REPL engine that controls execution      |
| `CommandRegistry`         | Manages command registration                  |
| `Command`                 | Represents a single command                   |
| `ArgumentParser`          | Parses and converts user input into arguments |
| `ArgumentParseException`  | Thrown for invalid or missing arguments       |
| `UnknownCommandException` | Thrown for unrecognized commands              |

---

## Command Examples

### Echo Command

```java
repl.getRegistry().register("echo", "Echoes input", (r, a) -> {
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
repl.getRegistry().register("config", "Parses flags", (r, a) -> {
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
repl.getRegistry().register(List.of("quit", "exit", "/q"), "Stops the REPL", (r, a) -> {
    r.stop();
    return 0;
});
```

---

### Argument Type Conversion

```java
repl.getRegistry().register("multiply", "Multiplies two integers", (r, a) -> {
    int x = a.next(int.class);
    int y = a.next(int.class);
    r.println("Result: " + (x * y));
    return 0;
});
```

---

### Help Command with Prefix

```java
repl.getRegistry().register("help", "Shows all or filtered commands", (r, a) -> {
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
repl.getRegistry().registerAsync("compute", "Performs async computation", (r, a) -> {
    Thread.sleep(500);
    return 42;
}, (r, result) -> r.println("Computed: " + result));
```

---

## License

[Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0)
