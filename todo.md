# Urgent TODO:
 - type checking for functions
 - semantic validation for function calls

# Problems:
 - Doing this /* **/ throws unclosed comment error because of how the lexing works
 - Comparison operation lambda functions are empty
 - (int) x ** y evaluates as (int) (x ** y), that may or may not be an issue
 - "let test = 1; let test2 = test + 1 + false;" is valid

# Priority Features:
 - Static typing

# Operations
 - Declaring strings in operations

# AST Pruning
 - Numeric expression solving
 - Loop simplifying

# Target Pipeline
- Lexing -> AST -> Validation -> Pruning -> IR -> Mlog

# Datatypes
 - Datatype validation:
  - Function return arg checking
  - Binary operation checking (check if both are the same type, or are able to be implicitly casted)
  - Unary operation checking (i.e. you cant have things like strings and booleans within - and + unary operations)
  - Type validation should be done in binary op pruning to reduce the number of steps
 - Datatype guessing (for doubles, ints and strings within operations)
 - Explicit and implicit casting:
  - Explicit casting should be checked semantically

# Custom Exception Handling
- Exception types:
 - Syntax, Semantic
- Multiple error messages in one compilation for when there are multiple errors (requires ditching java's exceptions)

# Compound Operators
 - += += -= *= /= %= &= ^= |= <<= >>= **=
 - for lexing and evaluating
 - i += x + y    is equivalent to   i = i + (x + y)

# Optimisation
 - Var simplification
 - Loops optimisation

# Technical Stuff
 - use encapsulation for node members instead of having separate variables to prevent errors
 - Support for non hardcoded evaluators
 - Unary operator orders
 - Add toggleable debugMode

# Possible Improvements
 - (Technical debt) Unaries, consts and binary operators being just one class may cause complications in the long run. 
  - OperationNode IS A MESS!
 - Migrate from using java's exceptions to a custom one with tree traversal
 - Docstrings can be improved
 - Validation functions can be unified to reduce compile time
 - add an instance method equals() within Token
 - unary operators are hardcoded and cannot be overloaded, implement a map for them to convert to binaries
 - operations with dynamic variables are not typed checked or parsed

# Syntax Document
 - A syntax document for Milyscript

# Future Features (low priority)
 - Arrays and structs
 - Libraries
 - Hardware recommendations (i.e. "this code requires at minimum 1 memory cell", etc)
 - Aliasing and Macros:
  - should be processed during the AST building stage
  - #define keyword
 - Inline raw mlog
 - Header files for declarations and configs