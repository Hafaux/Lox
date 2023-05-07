# Language Interpreter

Following along with the [Crafting Interpreters](https://craftinginterpreters.com/) book.

![Lexical Analygator](https://craftinginterpreters.com/image/scanning/lexigator.png)

## Syntactic Grammar Notes

```
expression -> litral | unary | binary | grouping ;
literal -> NUMBER | STRING | "true" | "false" | "nil" ;
grouping -> "(" expression ")" ;
unary -> ( "-" | "!" ) expression ;
binary -> expression operator expression ;
operator -> "==" | "!=" | "<" | "<=" | "+" | "-" | "*" | "/" ;
```

Quoted strings are exact terminals.[^1] Single lexeme whose text representation may vary are CAPITALIZED. Lowercase words are non-terminals.[^2]

[^1]: Letter from the grammar's alphabet. Tokens like `==`, `if`, `1234`, etc.\
[^2]: Named reference to another rule in the grammar. "Use" the rule and insert whatever it produces in its place.
