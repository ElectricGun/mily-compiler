# Mily Compiler

This program compiles the custom, C syntax programming language Mily to Mindustry Logic (mlog). 
The syntax looks pretty much like C/C#/Java/JS so there shouldn't be too much of a learning curve for anyone to use this language.

## Key Features
- The basic stuff (looping, if statements, variables)
- Functions such as `f(int  x) {return x}`
- Static and strict typing with minimal implicit casting e.g. `int x = 3 / 2;` is not allowed, use intdiv instead `int x = 3 // 2;`
- Comments (`/* */` and `--`)
- Full operations, such as `(x + y) * 3 - 9 * -z ** 2`
- A standard library imported using `#include std/bulb.mily;`
- Memory cell pointers: declared using any of the pointer datatypes below, derefenced using the std function r()

## Primitive Datatypes
- double
- int
- string
- boolean

## Pointer Datatypes
- p<int>
- p<double>
- p<boolean>

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
```