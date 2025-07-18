# Milyscript Mlog Compiler

This program is an implementation of Milyscript (a custom programming language) that compiles to Mindustry Logic (mlog).

## What is Milyscript?

Milyscript is a high-level programming language largely based on the C syntax (although very different).
It is designed in order to be easily usable for mlog coders.

### Key Features
- Functions
- Looping (for and while)
- Branching (if statements)
- Static typing and datatypes
- Optional dynamic typing (using `let`)
- Comments (`/* */` and `/-`)
- Type casting
- Variable declaration
- Full operations, such as `(x + y) * 3 - 9 * -z ** 2`
- Library imports (using #include)
- A standard library (bulb.mily) containing useful functions

### Other Features
- Compile-time expression optimisation, for example, `10 + 38 * (200 + 3 ** 3) // 3` gets simplified to `2885.0` on compile time.
- Compile-time semantic checking, including variable declaration and type checking.

## How it Works

This implementation works in 6 stages:
1. Lexing or tokenisation: raw strings of code is broken down into a list of tokens based on certain rules.
This stage, although rarely, may produce syntax errors e.g. on unclosed comments.
2. AST building: the abstract syntax tree is built by feeding it a string of tokens. The AST builds itself, starting from a single seed.
This stage produces syntax errors if the code is not written properly.
3. Semantic validation: the fully built AST is then read to check if the variables are logically correct i.e. they exist in the scopes they are in 
and their datatypes are consistent. Otherwise, this stage will produce semantic errors.
4. Pruning: expressions are simplified and unneeded nodes are cut.
5. Intermediate representation: the pruned AST is then used to produce pseucode-like representation.
6. Final transpilation: the IR is directly translated into the target bytecode, in this case, mlog.