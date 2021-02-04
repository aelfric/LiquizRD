package org.liquiz.parser;

public class ParserException extends RuntimeException {
  public ParserException(String s) {
    super(s);
  }

  public ParserException(Token lookahead) {
    super(String.format("Unexpected token: %s",lookahead.sequence));
  }
  public ParserException(Token lookahead, Throwable e) {
    super(String.format("Unexpected token: %s",lookahead.sequence), e);
  }
}
