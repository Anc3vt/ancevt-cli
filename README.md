# Ancevt CLI Framework

A lightweight but powerful Java framework for building **CLI tools** and **REPL (Read-Eval-Print Loop) applications**. It combines the simplicity of argument parsing with a flexible command registry and built-in REPL loop, giving you an **out-of-the-box interactive shell** in just a few lines.

This library is perfect for developer tools, admin consoles, embedded CLIs, or even educational projects.

---

## 🚨 Why not just Picocli or JLine?

| Feature                   | Ancevt CLI | Picocli | JLine         |
| ------------------------- | ---------- | ------- | ------------- |
| Lightweight               | ✅ Yes      | ❌ Heavy | ⚠️ Medium     |
| Async execution           | ✅ Built-in | ❌       | ❌             |
| Annotation-based commands | ✅ Yes      | ✅ Yes   | ❌             |
| Command builder API       | ✅ Yes      | ❌       | ❌             |
| Built-in REPL loop        | ✅ Yes      | ❌       | ⚠️ Only input |

With Ancevt CLI you don’t have to glue together multiple libraries — **everything you need is in one framework.**

---

## ✨ Features

* **Argument parsing** with support for:

  * Quoted strings (`"hello world"`)
  * Escaped characters (`line\ break` → `line break`)
  * `--key value` and `--key=value` syntax
* **Command registry** with builder API
* **Annotation-based commands** with `@ReplCommand` and `@ReplExecute`
* **Async command execution** with fallback to `ForkJoinPool`
* **Simple REPL runner** (`ReplRunner`) with pluggable input/output streams
* **Helpful error messages** for unknown commands or parse errors
* **Unit-tested** with JUnit 5

---

## 🚀 Quick Start

### Before: the old way

```java
Scanner sc = new Scanner(System.in);
while (true) {
    String line = sc.nextLine();
    if (line.equals("ping")) {
        System.out.println("pong");
    }
}
```

### After: with Ancevt CLI

```java
ReplRunner repl = new ReplRunner();
repl.getRegistry().command("ping")
    .description("Replies with pong")
    .action((r, a) -> { r.println("pong"); return 0; })
    .build();

repl.start();
```

Run it:

```
> ping
pong
```

---

### Annotation-based registration

```java
@ReplCommand(name = "sum", description = "Adds two integers")
public class SumCommand {
    @ReplExecute
    public Object run(ReplRunner repl, Arguments args) {
        int a = args.next(Integer.class, 0);
        int b = args.next(Integer.class, 0);
        int result = a + b;
        repl.println("Result: " + result);
        return result;
    }
}

CommandRegistry registry = new CommandRegistry();
registry.register(SumCommand.class);
new ReplRunner(registry).start();
```

Now:

```
> sum 10 15
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

Also works with quotes:

```java
Arguments args = Arguments.parse("--message \"Hello World\"");
System.out.println(args.get("--message")); // Hello World
```

---

## ⏳ Async Execution

```java
registry.command("download")
    .description("Simulates a background task")
    .action((r, a) -> {
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        return "done";
    })
    .async()
    .build();
```

```
> download
(done appears asynchronously)
```

---

## 🔮 Use Cases

* Interactive REPL shells for developer tools
* Admin consoles for applications
* Embeddable CLI for microservices
* Educational projects (parsing, REPL design, DSLs)
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

---

## 📬 Contact

[me@ancevt.com](mailto:me@ancevt.com)
