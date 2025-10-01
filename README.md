# Replines Core Library

[![Maven Central](https://img.shields.io/maven-central/v/com.ancevt.replines/replines-core.svg)](https://central.sonatype.com/artifact/com.ancevt.replines/replines-core)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)


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

| Feature                   | Replines   | Picocli | JLine         |
| ------------------------- |------------| ------- | ------------- |
| Lightweight               | ✅ Yes      | ❌ Heavy | ⚠️ Medium     |
| Async execution           | ✅ Built-in | ❌       | ❌             |
| Annotation-based commands | ✅ Yes      | ✅ Yes   | ❌             |
| Command builder API       | ✅ Yes      | ❌       | ❌             |
| Built-in REPL loop        | ✅ Yes      | ❌       | ⚠️ Only input |
| Colorized output (ANSI)   | ✅ Yes      | ❌       | ❌             |

With **Replines**, you don’t need to glue together multiple libraries — everything you need for a functional REPL/CLI is already here.

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
  <groupId>com.ancevt.replines</groupId>
  <artifactId>replines-core</artifactId>
  <version>1.1.0</version>
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

### After: with Replines

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

## 🪄 Reflection-based Argument Binding

Replines allows you to **bind parsed arguments directly into Java objects** using annotations and reflection. This eliminates boilerplate parsing code and gives you strongly typed arguments for your commands.

### Defining Argument Classes

You can mark fields in a class with `@CommandArgument` (for positional args) and `@OptionArgument` (for named options):

```java
class MyArgs {
    @CommandArgument
    String name;

    @OptionArgument(names = {"-c", "--count"}, required = true)
    int count;

    @OptionArgument(names = {"-f", "--flag"})
    boolean flag;
}
```

### Using with the Builder API

The command builder integrates seamlessly with argument binding:

```java
registry.command("greet")
    .description("Greets a user with options")
    .action(MyArgs.class, (repl, args) -> {
        repl.println("Hello, " + args.name + "!");
        repl.println("Count: " + args.count);
        repl.println("Flag: " + args.flag);
    })
    .build();
```

Now you can run:

```
> greet Alice --count 5 --flag
Hello, Alice!
Count: 5
Flag: true
```

### With Return Values

You can also return values from commands when binding arguments:

```java
static class SumArgs {
    @CommandArgument String a;
    @CommandArgument String b;
}

registry.command("sum")
    .description("Adds two numbers")
    .action(SumArgs.class, (repl, parsed) -> {
        int result = Integer.parseInt(parsed.a) + Integer.parseInt(parsed.b);
        repl.println("Result: " + result);
        return result;
    })
    .result((repl, result) -> repl.println("Returned: " + result))
    .build();
```

Output:

```
> sum 7 8
Result: 15
Returned: 15
```

---

### 🔄 Comparison with Picocli

Unlike **Picocli**, where argument binding is typically used for parsing command-line options of standalone applications, **Replines integrates reflection-based argument binding directly into its REPL command model**. This means:

* You don’t need to manually wire up argument parsers inside your REPL.
* Each command can have its own DTO with annotated fields.
* Binding works seamlessly both in sync and async commands.

This makes Replines particularly well-suited for **interactive shells** and **embedded admin consoles**, where commands are invoked repeatedly and need clean, strongly typed argument handling.


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

## 📜 License

Licensed under the Apache License, Version 2.0.

See [LICENSE](LICENSE) and [notice.md](notice.md) for details.

---

## 📬 Contact

[me@ancevt.com](mailto:me@ancevt.com)
