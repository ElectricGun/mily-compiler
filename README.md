# Mily Mlog Compiler

This program compiles the custom, C syntax programming language Mily to Mindustry Logic (mlog). 
The syntax looks pretty much like C/C#/Java/JS so there shouldn't be too much of a learning curve for anyone to use this language.

## Key Features
- Looping (for and while)
- Branching (if statements)
- Functions
- Static variable typing
- Strict typing with minimal implicit casting e.g. `int x = 3 / 2;` is not allowed, use intdiv instead `int x = 3 // 2;`
- Comments (`/* */` and `/-`)
- Type casting: (int), (boolean), etc
- Full operations, such as `(x + y) * 3 - 9 * -z ** 2`

## Datatypes
- double
- int
- string
- boolean

## Other Features
- Compile error stack tracing.
- Compile-time expression optimisation, for example, `10 + 38 * (200 + 3 ** 3) // 3` gets simplified to `2885` on compile time.
- Compile-time semantic checking, including variable declaration, type checking and function declaration checking.

## Example Code
### Factorials
```
int n = 5;
/- calculate factorial of 5
int curr = 1;
for (int i = 2; i <= n; i = i + 1) {
    curr = curr * i;
}
int result = curr; /- should be 120
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

### Count Even Numbers
```
int n = 1000;
/- count even numbers
int i = 0;
int j = 0;
while (i < n) {
    if (i % 2 == 0) {
        j = j + 1;
    }
    i = i + 1;
}
```