# v0.0.0-alpha
- Initial release

# v0.1.0-alpha
- Unified callable overloads
- Fixed error when having binary operators on function returns
- Changed std function as_int() to dtoi() for brevity
- Feature: Memory variables
  - Declared using `p<int>`, `p<double>`, `p<boolean>`
  - Dereferenced using `r()`
  - Example:
```
-- allocate memory to the variable "test"
p<int> test = 100;
-- fetch the variable value
print(r(test));
printflush(@message1);
-- prints out 100
```