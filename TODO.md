# Urgent TODO:
 - variable assignments for pointer variables
 - templates with return types shouldnt be able to be called outside of operations
 - pre ast macros, actual template parsing
 - declaring macros into variables for reusability
 - Arrays and structs

# Flags
 - shorthand flags such as -h, -p, -v, etc

# Optimize
 - Pointer variables declared inside a scope should be unallocated

# Problems:
 - Sometimes, multiple of the same errors are thrown on one token, specifically because validateTypesHelper is called in many functions within Validation
 - library circular dependencies
 - one word symbols can break up in tokenization which is not good e.g. @test+test will become @, test, +, test, 
 - add a way to write multi token symbols with literals, maybe @{} 

# Useful
 - compiler calls, such as fetching an environmental variable like an allocated memory cell using an index
 - pointers for memory variables
 - optimization hints invoked in the pruning stage, such as unused functions
 - multitoken variable names using \` \` literals

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

# Improvements
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
 - rewrite scopenode


# Syntax Document
 - A syntax document for Milyscript

# Future Features (low priority)
 - Hardware recommendations (i.e. "this code requires at minimum 1 memory cell", etc)
 - Byte packing, such as storing int8, int16, int32 inside a double
