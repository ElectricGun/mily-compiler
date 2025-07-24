# Milyscript Mlog Compiler

This program is an implementation of Milyscript (a custom programming language) that compiles to Mindustry Logic (mlog).

## What is this Language?

Milyscript is a high-level programming language largely based on the C syntax (although a bit different).
It is designed in order to be easily usable for mlog coders. This language is functional, not OOP.

### Key Features
- Functions
- Looping (for and while)
- Branching (if statements)
- Static variable typing
- Optional dynamic typing (using `let`)
- Comments (`/* */` and `/-`)
- Type casting: (int), (boolean), etc
- Full operations, such as `(x + y) * 3 - 9 * -z ** 2`
- Library imports (using #include)
- A standard library (bulb.mily) containing useful functions

### Other Features
- Compile-time expression optimisation, for example, `10 + 38 * (200 + 3 ** 3) // 3` gets simplified to `2885.0` on compile time.
- Compile-time semantic checking, including variable declaration and type checking.