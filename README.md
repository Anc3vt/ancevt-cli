# Ancevt CLI Framework

A lightweight but powerful Java framework for building **CLI tools** and **REPL (Read-Eval-Print Loop) applications**. It provides flexible argument parsing, command registration (both programmatic and annotation-driven), and optional asynchronous execution.

This library is suitable for embedding in developer tools, admin shells, scripting environments, or any situation where a structured, extensible command-line interface is required.

---

## ✨ Features

* **Argument parsing** with support for:

    * Quoted strings (`"hello world"`)
    * Escaped characters (`line\ break` → `line break`)
    * `--key value` and `--key=value` syntax
* **Command registry** with builder API
* **Annotation-based commands** with `@ReplCommand` and `@ReplExecute`
* **Async command execution** with fallback to `ForkJoinPool` if no custom executor provided
* **Simple REPL runner** (`ReplRunner`) with pluggable input/output streams
* **Helpful error messages** for unknown commands or parse errors
* **Unit-tested** with JUnit 5

---

## 🚀 Quick Start

#### Programmatic registration

```java
ReplRunner repl = new ReplRunner();
CommandRegistry registry = repl.getRegistry();

registry.command("echo")
    .description("Echoes back arguments")
    .action((r, a) -> {
        while (a.hasNext()) r.println(a.next());
        return 0;
    })
    .build();

repl.start();
```

Now you can run:

```
echo hello world
```

Output:

```
hello
world
```

#### Annotation-based registration

```java
@ReplCommand(name = "sum", description = "Adds two integers")
public class SumCommand {
    @ReplExecute
    public Object run(ReplRunner repl, Arguments args) {
        int a = args.hasNext() ? args.next(Integer.class) : 0;
        int b = args.hasNext() ? args.next(Integer.class) : 0;
        int result = a + b;
        repl.println("Result: " + result);
        return result;
    }
}

CommandRegistry registry = new CommandRegistry();
registry.register(SumCommand.class);
ReplRunner repl = new ReplRunner(registry);
repl.start();
```

Now you can run:

```
sum 10 15
```

Output:

```
Result: 25
```

---

## ⚙️ Argument Parsing

```java
Arguments args = Arguments.parse("--port=8080 --debug true");

int port = args.get(Integer.class, "--port"); // 8080
boolean debug = args.get(Boolean.class, "--debug"); // true
```

Supported forms:

* `--key value`
* `--key=value`
* `--flag`

Quoted values are also supported:

```java
Arguments args = Arguments.parse("--message \"Hello World\"");
System.out.println(args.get("--message")); // Hello World
```

---

## ⏳ Async Execution

Mark a command as asynchronous either via the builder or annotation:

```java
registry.command("download")
    .description("Simulates a background task")
    .action((r, a) -> {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}
        return "done";
    })
    .async()
    .build();
```

This command will execute in a background thread, and its result will be printed (or handled by a custom result consumer).

---

## 🔮 Use Cases

* Interactive REPL shells for developer tools
* Admin consoles for applications
* Embeddable CLI for microservices
* Educational projects (teaching parsing, REPL design, DSLs)
* Replacement for ad-hoc `Scanner.nextLine()` loops

---

## 📦 Package Overview

```
com.ancevt.cli.argument
  ├── Arguments              // Argument parser
  ├── ArgumentParseException // Custom exception
  └── ArgumentSplitHelper    // Tokenizer

com.ancevt.cli.repl
  ├── ReplRunner             // Main REPL loop
  ├── CommandRegistry        // Stores commands
  ├── Command                // Command definition
  ├── UnknownCommandException// Error type
  └── annotation             // @ReplCommand, @ReplExecute
```

---

## 📜 License

Licensed under the Apache License, Version 2.0.

See [LICENSE](LICENSE) and [notice.md](notice.md) for details.

## Contact me:
[me@ancevt.com](mailto:me@ancevt.com())