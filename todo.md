# Urgent TODO:
 - Raw template validation
 - Function overloads
 - Create a string counter for generated variables to prevent conflicts

# Problems:
 - (int) x ** y evaluates as (int) (x ** y), that may or may not be an issue
 - "let test = 1; let test2 = test + 1 + false;" is valid
 - Sometimes, multiple of the same errors are thrown on one token, specifically because validateTypesHelper is called in many functions within Validation

# Operations
 - Declaring strings in operations
 - Explicit casting should compile into functions essentially

# Useful
 - Inline raw mlog (can be used for defining mlog commands in libraries)
 - pointers for memory variables

# Semantic Checking
 - Check for unreachable code

# AST Pruning
 - Numeric expression solving with variables
 - Loop simplifying

# Target Pipeline
- Lexing -> AST -> Validation -> Pruning -> IR -> Mlog

# Datatypes
 - Datatype validation
 - Constants (final keyword) 

# Custom Exception Handling
- Exception types:
 - Syntax, Semantic
- Multiple error messages in one compilation for when there are multiple errors (requires ditching java's exceptions)

# Compound Operators
 - += += -= *= /= %= &= ^= |= <<= >>= **=
 - for lexing and evaluating
 - i += x + y    is equivalent to   i = i + (x + y)

# Loops
 - break
 - continue
    
# Technical Stuff
 - use encapsulation for node members instead of having separate variables to prevent errors
 - Support for non hardcoded evaluators
 - Unary operator orders
 - Add toggleable debugMode

# Possible Improvements
 - (Technical debt) Unaries, consts and binary operators being just one class may cause complications in the long run. 
  - OperationNode IS A MESS!
 - Docstrings can be improved
 - Validation functions can be unified to reduce compile time
 - add an instance method equals() within Token instead of calling keyEquals()
 - operations with dynamic variables are not typed checked or parsed
 - scope validation can be simplified
 - current variable naming in mlog may lead to issues 
 - rewrite Lexing

# Syntax Document
 - A syntax document for Milyscript

# Future Features (low priority)
 - Arrays and structs
 - Libraries
 - Hardware recommendations (i.e. "this code requires at minimum 1 memory cell", etc)
 - Aliasing and Macros:
  - should be processed during the AST building stage
  - #define keyword
 - Header files for declarations and configs