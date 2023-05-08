# Language Interpreter

Following along with the [Crafting Interpreters](https://craftinginterpreters.com/) book.

![Lexical Analygator](https://craftinginterpreters.com/image/scanning/lexigator.png)

## Syntactic Grammar Notes

### Initial grammar

```
expression -> litral | unary | binary | grouping ;
literal -> NUMBER | STRING | "true" | "false" | "nil" ;
grouping -> "(" expression ")" ;
unary -> ( "-" | "!" ) expression ;
binary -> expression operator expression ;
operator -> "==" | "!=" | "<" | "<=" | "+" | "-" | "*" | "/" ;
```

Quoted strings are exact terminals.[^1] Single lexeme whose text representation may vary are CAPITALIZED. Lowercase words are non-terminals.[^2]

This grammar allows the production of strings like `(4 + 9) / !1 == nil`, but it's "ambiguous". The binary rule allows nesting in both ways, which allows you to generate the same string but with a different syntax tree. Take the string `6 / 3 - 1` for example. You can produce it by:

1. Starting with expression, picking `binary`
2. For the left `expression`, picking `NUMBER`, and using 6
3. For the `operator`, pick `/`
4. For the right `expression`, pick `binary` again.
5. In the nested `binary` pick `3 - 1`

But you can also produce the same string in another way:

1. Begin with expresssion, pick `binary`
2. For the right `expression` pick `NUMBER` and use 1
3. For the `operator` pick `-`
4. For the left `expression` pick `binary`
5. Then in the nested `binary` pick `6 / 3`

The string is the same, but the generated syntax tree is different. In other words there are no rules for precedence or associativity. We will apply the same precedence rules C, going from lowest to highest.

| Name       | Operators | Associates |
| ---------- | --------- | ---------- |
| Equality   | == !=     | Left       |
| Comparison | > >= < <= | Left       |
| Term       | - +       | Left       |
| Factor     | / \*      | Left       |
| Unary      | ! -       | Right      |

We will define a separate rule for each level of precedence. Each rule below matches expressions at its precedence level or higher. For example a `term` can match `4 + 6`, but also `3 * 2 / 6`. The final `primary` rule covers the highest-precedence - literals and expressions inside parenthesis.

```
expression -> equality
equality -> comparison ( ( "==" | "!=" ) comparison )* ;
comparison -> term ( ( ">" | "<" | ">=" | "<=" ) term )* ;
term -> factor ( ( "+" | "-" ) factor )* ;
factor -> unary ( ( "*" | "/" ) unary )* ;
unary -> ( "!" | "-" ) unary | primary ;
primary -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
```

[^1]: Letter from the grammar's alphabet. Tokens like `==`, `if`, `1234`, etc.\
[^2]: Named reference to another rule in the grammar. "Use" the rule and insert whatever it produces in its place.
