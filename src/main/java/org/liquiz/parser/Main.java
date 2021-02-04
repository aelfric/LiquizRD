package org.liquiz.parser;

import java.util.List;

public class Main {
  public static void main(String[] args) {
    Tokenizer tokenizer = new Tokenizer();
    tokenizer.add("sin|cos|exp|ln|sqrt", 1); // function
    tokenizer.add("\\(", 2); // open bracket
    tokenizer.add("\\)", 3); // close bracket
    tokenizer.add("[+-]", 4); // plus or minus
    tokenizer.add("[*/]", 5); // mult or divide
    tokenizer.add("\\^", 6); // raised
    tokenizer.add("[0-9]+", 7); // integer number
    tokenizer.add("[a-zA-Z][a-zA-Z0-9_]*", 8); // variable

    List<Token> tokens = tokenizer.tokenize("4+5+x");
    System.out.println(tokens);
  }
}
