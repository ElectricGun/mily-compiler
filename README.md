<h1 align="center">
    Mily Compiler
</h1>

<div align="center">

![Stars](https://img.shields.io/github/stars/electricgun/mily-compiler)
[![Github All Releases](https://img.shields.io/github/downloads/electricgun/mily-compiler/total.svg)]()
[![Changelog](https://img.shields.io/badge/changelog-md-blue.svg)](CHANGELOG.md)

</div>

This program compiles the high-level programming language Mily to Mindustry Logic (mlog) syntax bytecode. 
The syntax looks pretty much like C/C#/Java/JS so there shouldn't be too much of a learning curve for anyone using this language.

## Disclaimer

This project is currently in alpha and is very much work in progress; the code is still quite messy. 
I would appreciate it if anyone could do a little testing and bug hunting to polish out any "unintended features".
If you find a bug, please create an issue containing the Mily code with a brief explanation regarding the issue.

The alpha versions are unstable, and code syntax may change on updates!

Do note that the standard library is not currently implemented as a part of the compiler, but a set of external files located in `tests/std/`.
Be sure to download the `std/` folder and move it to the working directory of your project before importing them.

## Table of Contents

* [Table of Contents](#table-of-contents)
* [Usage](#usage)
  * [Flags](#flags)
  * [Examples](#examples)
* [Key Features](#key-features)
* [Datatypes](#datatypes)
  * [Primitive Datatypes](#primitive-datatypes)
  * [Processor Datatypes](#processor-datatypes)
  * [Reference Datatypes](#reference-datatypes)
  <!-- * [Array Reference Datatypes](#array-reference-datatypes) -->
* [Other Features](#other-features)
* [Example Code](#example-code)

## Usage
### Flags
| Flag                | Type   | Description                                         |
|---------------------|--------|-----------------------------------------------------|
| --help              | Bool   | Prints the help page                                |
| --debug             | Bool   | Print very convoluted logs                          |
| --quiet             | Bool   | Disable descriptive prints                          |
| --benchmark         | Bool   | Print benchmark                                     |
| --output            | String | Output directory (folder)                           |
| --print-ast         | Bool   | Print final AST                                     |
| --print-output      | Bool   | Print compiled output                               |
| --no-confirm        | Bool   | Disable confirmations                               |
| --generate-comments | Bool   | Enabled system generated comments in compiled code) |

### Examples
Replace `<main.mily>` with the directory of your main Mily file, and `Mily.jar` with your Mily compiler jar executable.
#### Compile and print output to terminal 
```shell
    java -jar Mily.jar <main.mily> --quiet --print-output 
```
#### Compile and output to directory
Replace `<output-dir>` with the directory of the output folder
```shell
    java -jar Mily.jar <main.mily> --quiet --output <output-dir>
```

#### Compile and output to directory without overwrite confirmation
```shell
    java -jar Mily.jar <main.mily> --quiet --no-confirm --output <output-dir>
```

## Key Features
- The basic stuff (looping, if statements, variables)
- Functions such as `f(int  x) {return x}`
- Static and strict typing with minimal implicit casting e.g. `int x = 3 / 2;` is not allowed, use intdiv instead `int x = 3 // 2;`
- Comments (`/* */` and `--`)
- Full operations, such as `(x + y) * 3 - 9 * -z ** 2`
- A standard library imported using `#include std/bulb.mily;`
- Memory cell pointers: declared using any of the pointer datatypes below, derefenced using the std function r()

## Datatypes

### Primitive Datatypes
Basic datatypes, able to be stored anywhere.
- double
- int
- boolean

### Processor Datatypes
These datatypes may only be stored within the processor's variable map.
- string

### Reference Datatypes
Reference variables store the index of the value stored within a memory cell instead of the value itself. Only primitive datatypes may be stored in memory.
- ptr<type>

To access their value, call the dereference function `r(var)` found in the standard library.

<!--
### Array Reference Datatypes [WIP]
Complex datatypes. Able to store many primitive values.
- [WIP] arr<type> ... [length] - Contiguous array of size `length`. Fixed size but fast element accessing - O(1).
- [WIP] lnlist<type> - Linked list. Dynamic size, but slow element accessing - O(n).
- [WIP] blnlist<type> ... [blocksize] - Blocked linked list. Essentially a linked list of contiguous arrays of size `blocksize`. 
A larger block size will result in a faster element access speed, but more memory space.
Access a value at index `n` using the array dereference function `ar(array, n)` found in the standard library
-->

## Other Features
- Compile error stack tracing.
- Compile-time expression optimisation, for example, `10 + 38 * (200 + 3 ** 3) // 3` gets simplified to `2885` on compile time.
- Compile-time semantic checking, including variable declaration, type checking and function declaration checking.

## Example Code
### Factorials
```
int n = 5;
int curr = n;
for (int i = 1; i < n; i = i + 1) {
    curr = curr * i;
}
int result = curr;
```

### Nested Loops
```
int x_ = 0;
for (int i = 0; i < 100; i = i + 1) {
    for (int j = 0; j < 100 - i + x_; j = j + 1) {
        int k = j;
        while (k < 100) {
            k = k + 1;
            x_ = x_ + 1;
        }
    }
}
```

### Nested Functions
```
-- Nested Function
int f(int x, int y) {
    int g(int z) {
        return 100;
    }
    
    return x + g(y);
}
```

### Print Even Numbers
```
#include std/bulb.mily;
int i = 0;
while (i < 1000) {
    print(i % 2 == 0);
    i = i + 1;
}
printflush(@message1);
```