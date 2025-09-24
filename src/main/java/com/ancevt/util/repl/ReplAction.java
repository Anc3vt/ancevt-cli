package com.ancevt.util.repl;


import com.ancevt.util.repl.args.Args;

@FunctionalInterface
public interface ReplAction {

    void execute(Repl repl, Args args);
}
