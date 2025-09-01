# Urgent TODO:
 - unify argument parsing for CallableNodes
 - templates with return types shouldnt be able to be called outside of operations
 - rewrite scopenode
 - "any" datatype shouldnt be able to be used outside of template args
 - implement "any" datatype. requires to be casted everytime it is used with anything that is "any"

# Flags
 - shorthand flags such as -h, -p, -v, etc

# Problems:
 - Sometimes, multiple of the same errors are thrown on one token, specifically because validateTypesHelper is called in many functions within Validation
 - library circular dependencies

# Useful
 - declaring macros into variables for reusability
 - pointers for memory variables
 - pre-ast macros
 - optimization hints invoked in the pruning stage, such as unused functions

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
 - its better to store function overrides in lists within a dictionary, using its name as the key
 - Make unary and constant setting neater in OperationNode
 - Replace all keyEquals(x, token) with token.keyEquals()

# Syntax Document
 - A syntax document for Milyscript

# Future Features (low priority)
 - Arrays and structs
 - Hardware recommendations (i.e. "this code requires at minimum 1 memory cell", etc)
 - Macros (before AST stage)
 - Templates (after AST stage)
 - actual template/macro parsing into AST
