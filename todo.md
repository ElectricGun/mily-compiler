# Start compiling to mlog once all of these features are done:
 - AST Pruning
 - AST Validation

# Compound Operators
 - += += -= *= /= %= &= ^= |= <<= >>= **=
 - for lexing and evaluating
 - i += x + y    is equivalent to   i = i + (x + y) 

# Datatypes
 - (int, double, boolean). Mlog doesn't really support that many datatypes, but implementing them here should be nice
 - static typing

# Functions
 - return types, such as void, int, double, boolean.

# Aliasing and Macros
 - should be processed during the AST building stage
 - #define keyword

# Technical stuff
 - use encapsulation for node members instead of having separate variables to prevent errors
 - Support for non hardcoded evaluators
 - Unary operator orders

# Possible improvements
 - (Technical debt) Unaries, consts and binary operators being just one class may cause complications in the long run. 
 - Migrate from using java's exceptions with a custom one with tree traversal

# Correct Pipeline
 - Lexixng -> AST -> Validation -> Pruning -> IR
