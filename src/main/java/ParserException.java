public class ParserException extends RuntimeException {
  public ParserException(String s) {
    super(s);
  }

  public ParserException(String s, Token lookahead) {
    super(String.format(s,lookahead.sequence));
  }
}
