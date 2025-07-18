# Priority Features:
 - Numeric expression solving
 - Datatypes

# AST Pruning
 - Numeric expression solving
 - Loop simplifying

# Datatypes
 - Static typing
 - Datatype validation:
  - Function return arg checking
  - Binary operation checking (check if both are the same type, or are able to be implicitly casted)
  - Unary operation checking (i.e. you cant have things like strings and booleans within - and + unary operations)
 - Datatype guessing (for doubles, ints and strings within operations)
 - Explicit and implicit casting:
  - Explicit casting should be checked semantically 

# Compound Operators
 - += += -= *= /= %= &= ^= |= <<= >>= **=
 - for lexing and evaluating
 - i += x + y    is equivalent to   i = i + (x + y)

# Functions
 - return types, such as void, int, double, boolean.

# Aliasing and Macros
 - should be processed during the AST building stage
 - #define keyword

# Technical Stuff
 - use encapsulation for node members instead of having separate variables to prevent errors
 - Support for non hardcoded evaluators
 - Unary operator orders
 - Multiple error messages in one compilation for when there are multiple errors (requires ditching java's exceptions)

# Possible Improvements
 - (Technical debt) Unaries, consts and binary operators being just one class may cause complications in the long run. 
 - Migrate from using java's exceptions to a custom one with tree traversal
 - Docstrings can be improved

# Syntax Document
 - A syntax document for Milyscript

# Correct Pipeline
 - Lexixng -> AST -> Validation -> Pruning -> IR
