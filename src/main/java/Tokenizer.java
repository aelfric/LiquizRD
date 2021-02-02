import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
  private final List<Token> tokens;
  private final List<TokenInfo> tokenInfos;

  public Tokenizer() {
    tokenInfos = new LinkedList<>();
    tokens = new LinkedList<Token>();
  }

  public void add(String regex, int token) {
    tokenInfos.add(
        new TokenInfo(
            Pattern.compile("^(" + regex + ")"), token));
  }

  public List<Token> tokenize(String str) {
    String s = str;
    tokens.clear();
    while (!s.equals("")) {
      boolean match = false;
      for (TokenInfo info : tokenInfos) {
        Matcher m = info.regex.matcher(s);
        if (m.find()) {
          match = true;

          String tok = m.group();
          tokens.add(new Token(info.token, tok));

          s = m.replaceFirst("");
          break;
        }
      }
      if (!match) throw new ParserException(
          "Unexpected character in input: "+s);
    }
    return tokens;
  }

  private static class TokenInfo {
    public final Pattern regex;
    public final int token;

    public TokenInfo(Pattern regex, int token) {
      super();
      this.regex = regex;
      this.token = token;
    }
  }
}