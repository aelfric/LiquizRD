import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LongTextQuestion implements QuestionElement {
  public final String answer;

  public LongTextQuestion(String declaration) {
    final Pattern compile = Pattern.compile("\\$tar:([^$]+)\\$");

    final Matcher matcher = compile.matcher(declaration);
    if(matcher.find()) {
      answer = matcher.group(1);
    } else {
      throw new ParserException("Could not decode fill-in question");
    }
  }
}
