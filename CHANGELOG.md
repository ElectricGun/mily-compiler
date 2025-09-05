# v0.0.0-alpha
- Initial release

# v0.1.0-alpha
- New Feature: Reference variables
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
- Changed template call syntax to be the same as function calls
- Changed macro literals from `$ $` to `${ }`
- Fixed exception when having binary operators on function returns
- Fixed empty void function throwing an error
- Changed std function `as_int` to `dtoi` for brevity