# Urgent TODO:
 - OperationMap; map for operations and casting
 - casts should be able to be converted into binary operations, with the datatype as the 2nd operand
 - type checking for functions

# Problems:
 - Doing this /* *********/ throws unclosed comment error
 - division always returns a double, so int x = 5 / 7 (should be intentional, because // intdiv exists)
 - Comparison operation lambda functions are empty

# Priority Features:
 - Static typing
 - Datatype validation
 - Datatype guessing
 - Explicit and implicit casting

# AST Pruning
 - Numeric expression solving
 - Loop simplifying

# Planned Pipeline
- Lexing -> AST -> Validation -> Pruning -> IR -> Mlog

# Actual Pipeline
- Lexing -> AST -> Unary to Binary -> Validation -> Pruning

# Datatypes
 - Static typing
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
 - Pruning::operationsParserMap

# Syntax Document
 - A syntax document for Milyscript

# Future Features (low priority)
 - Arrays
 - Structs and Classes
 - Hardware recommendations (i.e. "this code requires at minimum 1 memory cell", etc)
 - Aliasing and Macros:
  - should be processed during the AST building stage
  - #define keyword
 - Inline raw mlog