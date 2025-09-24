# REPL CLI

A simple yet flexible Java-based REPL (Read-Eval-Print Loop) framework with typed argument parsing, pluggable commands, and an extensible architecture for building developer-friendly CLI tools or embedded scripting interfaces.

## ✨ Features

* 🔄 **REPL loop**: customizable command loop, input/output streams.
* 🧠 **Args parser**: type-safe argument parsing with quotes, escape sequences, and option-style key lookup.
* 🔌 **Pluggable commands**: define commands with `ReplAction`, register in `CommandSet`.
* 📜 **Formatted help**: auto-formatted command listings and descriptions.
* ⚠️ **Error handling**: safe fallback for missing commands with `NoSuchCommandException`.

## 🚀 Quick Start

```java
CommandSet commandSet = new CommandSet();

commandSet.add("hello", (repl, args) -> {
    repl.println("Hello, world!");
});

commandSet.add("echo", "Echo input back", (repl, args) -> {
    while (args.hasNext()) {
        repl.print(args.next() + " ");
    }
    repl.println("");
});

Repl repl = new Repl(commandSet);
repl.start(System.in, System.out);
```

## 🧰 Components


### Command System

* `Command` — binds a word to a `ReplAction`
* `CommandSet` — maintains the list and renders help output
* `ReplAction` — functional interface: `(Repl repl, Args args) -> {}`

### Repl Loop

* Reads input line-by-line
* Tokenizes using `Args`
* Matches commands via `CommandSet`
* Handles unknown input via `NoSuchCommandException`


---
Contact me:
[me@ancevt.com](mailto:me@ancevt.com)

