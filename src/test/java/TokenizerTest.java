import org.junit.jupiter.api.Test;
import org.liquiz.parser.Parser;
import org.liquiz.parser.Token;
import org.liquiz.parser.Tokenizer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

class TokenizerTest {
  @Test
  void testTokenize() {
    Tokenizer tokenizer = new Tokenizer();
    tokenizer.add("sin|cos|exp|ln|sqrt", 1); // function
    tokenizer.add("\\(", 2); // open bracket
    tokenizer.add("\\)", 3); // close bracket
    tokenizer.add("[+-]", 4); // plus or minus
    tokenizer.add("[*/]", 5); // mult or divide
    tokenizer.add("\\^", 6); // raised
    tokenizer.add("[0-9]+", 7); // integer number
    tokenizer.add("[a-zA-Z][a-zA-Z0-9_]*", 8); // variable

    assertThat(tokenizer.tokenize("4+5+x"), hasSize(5));
  }

  @Test
  void testLiquizTokenize() {
    InputStream resourceAsStream = TokenizerTest.class.getResourceAsStream("demo.lq");

    String text = new BufferedReader(
        new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));
    Parser parser = new Parser();
    List<Token> tokens = parser.tokenize(text);
    parser.parse(tokens);
  }
}