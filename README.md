# REPL Framework for Java

A lightweight and extensible REPL (Read-Eval-Print Loop) framework for building interactive CLI applications in Java.

## Features

* Command registration with single or multiple aliases
* Typed argument parsing (supports `int`, `boolean`, `double`, `String`, etc.)
* Quoted and escaped arguments supported
* Control REPL lifecycle: start, stop, and execute
* Minimal setup, easy integration into any Java application

---

## Getting Started

```java
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        ReplRunner repl = new ReplRunner();

        repl.getRegistry()
                // Register command #1    
                .register("hello", "Prints a greeting", (r, a) -> {
                    r.println("Hello from the REPL!");
                })
                // Register command #2    
                .register("add", "Adds two numbers", (r, a) -> {
                    int a1 = a.next(int.class);
                    int a2 = a.next(int.class);
                    r.println("Sum: " + (a1 + a2));
                })
                // Register command #3    
                .register(List.of("exit", "e"), "Stops the REPL", (r, a) -> r.stop());

        repl.start();
    }
}
```

---

## Argument Parsing Example

```java
ArgumentParser parser = ArgumentParser.parse("--port 8080 --debug true");

int port = parser.get(int.class, "--port");
boolean debug = parser.get(boolean.class, "--debug");
```

### With quoted values:

```java
ArgumentParser parser = ArgumentParser.parse("send \"Hello world\" user42");

String message = parser.next();  // Hello world
String user = parser.next();     // user42
```

---

## Core Components

* `ReplRunner` — The main REPL engine
* `CommandRegistry` — Stores and manages registered commands
* `Command` — Represents a single REPL command
* `CommandHandler` — Functional interface for command handlers
* `ArgumentParser` — Parses raw input into typed arguments
* `ArgumentParseException` — Exception thrown on parsing errors
* `UnknownCommandException` — Exception for unrecognized commands

---

# REPL Framework Examples

Here are some additional usage examples demonstrating how to integrate and extend the REPL framework.

---

## Example 1: Echo Command

```java
repl.getRegistry().register("echo", "Echoes back the input", (r, a) -> {
    while (a.hasNext()) {
        r.print(a.next() + " ");
    }
    r.println("");
});
```

**Usage:**

```
echo Hello World from REPL
```

**Output:**

```
Hello World from REPL
```

---

## Example 2: Flag Parsing

```java
repl.getRegistry().register("config", "Parses flags", (r, a) -> {
    if (a.contains("--debug")) {
        r.println("Debug mode enabled");
    }
    String mode = a.get("--mode", "default");
    r.println("Mode: " + mode);
});
```

**Usage:**

```
config --debug --mode production
```

**Output:**

```
Debug mode enabled
Mode: production
```

---

## Example 3: Using Multiple Aliases

```java
repl.getRegistry().register(Arrays.asList("quit", "exit", "/q"), "Terminates the REPL", (r, a) -> r.stop());
```

**Usage:**

```
exit
```

---

## Example 4: Argument Type Conversion

```java
repl.getRegistry().register("multiply", "Multiplies two integers", (r, a) -> {
    int x = a.next(int.class);
    int y = a.next(int.class);
    r.println("Result: " + (x * y));
});
```

**Usage:**

```
multiply 4 7
```

**Output:**

```
Result: 28
```

---

## Example 5: Help Command with Prefix Filter

```java
repl.getRegistry().register("help", "Shows all or filtered commands", (r, a) -> {
    String prefix = a.hasNext() ? a.next() : "";
    r.println(r.getRegistry().formattedCommandList(prefix));
});
```

**Usage:**

```
help
help mu
```

**Output:**

```
Available commands:
  echo                 Echoes back the input
  config               Parses flags
  multiply             Multiplies two integers
  quit                 Terminates the REPL

Available commands starting with 'mu':
  multiply             Multiplies two integers
```

---

These examples cover basic, intermediate, and advanced usage. You can easily extend the framework with custom commands, argument parsing, and REPL behaviors.


## License

[Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0)
