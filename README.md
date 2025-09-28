# Ancevt CLI Library

A lightweight but powerful Java library for building **CLI tools** and **REPL (Read-Eval-Print Loop) applications**. It combines simple **argument parsing**, a **flexible command registry**, and a built-in **interactive shell**, giving you a production-ready CLI in just a few lines.

This library is perfect for developer tools, admin consoles, embedded CLIs, or even educational projects where you want to experiment with command interpreters.

> **Note on terminology:**  
> This project uses the term **REPL (Read–Eval–Print Loop)** in the **shell-style sense**:  
> each line of input is read, matched against registered commands, executed,  
> and the result is printed back to the user.
>
> It does **not** try to be a general-purpose language interpreter like Python or JavaScript REPLs.  
> Instead, it’s closer to tools such as `bash`, `redis-cli`, or `psql` —  
> interactive command environments built around a registry of commands.


---

## 🚨 Why not just Picocli or JLine?

| Feature                   | Ancevt CLI | Picocli | JLine         |
| ------------------------- | ---------- | ------- | ------------- |
| Lightweight               | ✅ Yes      | ❌ Heavy | ⚠️ Medium     |
| Async execution           | ✅ Built-in | ❌       | ❌             |
| Annotation-based commands | ✅ Yes      | ✅ Yes   | ❌             |
| Command builder API       | ✅ Yes      | ❌       | ❌             |
| Built-in REPL loop        | ✅ Yes      | ❌       | ⚠️ Only input |
| Colorized output (ANSI)   | ✅ Yes      | ❌       | ❌             |

With **Ancevt CLI**, you don’t need to glue together multiple libraries — everything you need for a functional REPL/CLI is already here.

---

## ✨ Features

* **Argument parsing** with support for:

  * Quoted strings (`"hello world"`)
  * Escaped characters (`line\ break` → `line break`)
  * `--key value` and `--key=value` syntax
  * Flags (`--debug` → `true`)
* **Command registry** with fluent builder API
* **Annotation-based commands** with `@ReplCommand` and `@ReplExecute`
* **Async command execution** with configurable `Executor`
* **Simple REPL runner** (`ReplRunner`) with pluggable input/output
* **Output filters**, including a built-in **colorizer** (`<r>`, `<g>`, `<bold>` → ANSI)
* **Helpful error messages** for unknown commands or parse errors
* **Unit-tested** with JUnit 5

---

## 📥 Installation
```xml
<dependency>
  <groupId>com.ancevt.cli</groupId>
  <artifactId>ancevt-cli</artifactId>
  <version>1.0.0</version>
</dependency>
```

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
    .action((r, a) -> r.println("pong"))
    .build();

repl.start();
```

Run it:

```
> ping
pong
```

---

## 🧩 Command Registration

### Builder API

```java
registry.command("greet", "hello") // multiple aliases
    .description("Greets a user")
    .action((r, a) -> {
        String name = a.hasNext() ? a.next() : "stranger";
        r.println("Hello, " + name + "!");
    })
    .build();

// Usage:
// > greet Alice
// Hello, Alice!
// > hello
// Hello, stranger!
```

### Annotation-based

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

---

## ⚙️ Argument Parsing

### Basic usage

```java
Arguments args = Arguments.parse("--port=8080 --debug true");

int port = args.get(Integer.class, "--port"); // 8080
boolean debug = args.get(Boolean.class, "--debug"); // true
```

### Supported forms

* `--key value`
* `--key=value`
* `--flag`

### Quoted strings

```java
Arguments args = Arguments.parse("--message \"Hello World\"");
System.out.println(args.get("--message")); // Hello World
```

### Iteration

```java
Arguments args = Arguments.parse("one two three");
while (args.hasNext()) {
    System.out.println(args.next());
}
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

// > download
// (done appears asynchronously)
```

You can also provide a **result handler**:

```java
registry.command("compute")
    .action((r, a) -> 42)
    .result((r, result) -> r.println("Answer: " + result))
    .async()
    .build();
```

---

## 🎨 Colorized Output

The built-in **ColorizeFilter** lets you add ANSI tags:

```java
repl.addOutputFilter(new ColorizeFilter()::colorize);
repl.println("<g>Success!<reset>");
```

Available tags:

* `<r>`, `<g>`, `<y>`, `<b>`, `<c>`, `<w>` (colors)
* `<bold>`, `<underline>`, `<blink>`
* `<reset>` or `<>`

---

## 🛠 Default Commands

When using the builder API:

```java
ReplRunner repl = ReplRunner.builder()
    .withDefaultCommands()
    .withColorizer()
    .build();
```

You get:

* `help` → shows available commands
* `exit` or `/q` → stops the REPL

---

## 🔮 Use Cases

* Interactive REPL shells for developer tools
* Admin consoles for applications
* Embeddable CLI for microservices
* Educational projects (argument parsing, REPL design, DSLs)
* Replacement for ad-hoc `Scanner.nextLine()` loops
* Test harnesses for experiments and simulations

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
  ├── ReplRunnerBuilder      // Fluent builder
  └── annotation             // @ReplCommand, @ReplExecute

com.ancevt.cli.filter
  └── ColorizeFilter         // ANSI color tags
```

---

## 📜 License

Licensed under the Apache License, Version 2.0.

See [LICENSE](LICENSE) and [notice.md](notice.md) for details.

---

## 📬 Contact

[me@ancevt.com](mailto:me@ancevt.com)
