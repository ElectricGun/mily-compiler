# Urgent TODO:
 - template invokes in operations
 - Make unary and constant setting neater in OperationNode
 - Replace all keyEquals(x, token) with token.keyEquals()
 - Function arg names may cause conflicts
 - append "user_" to user specified variables to avoid conflicts with generated ones
 - Remove explicit casting entirely, its making OperationNode confusing, just use functions or something

# Flags
 - --help flag
 - shorthand flags such as -h, -p, -v, etc

# Problems:
 - (int) x ** y evaluates as (int) (x ** y), that may or may not be an issue
 - "let test = 1; let test2 = test + 1 + false;" is valid
 - Sometimes, multiple of the same errors are thrown on one token, specifically because validateTypesHelper is called in many functions within Validation
 - Cannot declare a string var, such as string x = "1", it regards "1" as a variable name;
 - library circular dependencies

# Useful
 - Inline raw mlog (can be used for defining mlog commands in libraries)
 - Type hinting system for raw mlog blocks if they return something
 - pointers for memory variables
 - pre-ast macros

# Semantic Checking
 - Check for unreachable code

# AST Pruning
 - Numeric expression solving with variables
 - Loop simplifying
 - Prune unused functions

# Datatypes
 - Constants (final keyword)
 - Ambiguous datatypes intended for templates, such as: int | double, int | double | string, int | boolean, etc.

# Compound Operators
 - += += -= *= /= %= &= ^= |= <<= >>= **=
 - for lexing and evaluating
 - i += x + y    is equivalent to   i = i + (x + y)

# Loops
 - break
 - continue
 - do while
    
# Technical Stuff
 - Support for non hardcoded evaluators
 - Unary operator orders
 - Add toggleable debugMode

# Possible Improvements
 - (Technical debt) Unaries, consts and binary operators being just one class may cause complications in the long run. 
  - OperationNode IS A MESS!
 - Docstrings can be improved
 - add an instance method equals() within Token instead of calling keyEquals()
 - scope validation can be simplified
 - current variable naming may lead to conflicts in output mlog 
 - rewrite Lexing

# Syntax Document
 - A syntax document for Milyscript

# Future Features (low priority)
 - Arrays and structs
 - Hardware recommendations (i.e. "this code requires at minimum 1 memory cell", etc)
 - Macros (before AST stage)
 - Templates (after AST stage)
 - actual template/macro parsing into AST
