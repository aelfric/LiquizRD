import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FillInQuestion implements QuestionElement {
  public final int length;
  public final String answer;

  public FillInQuestion(String declaration) {
    final Pattern compile = Pattern.compile("\\$fQ\\{(\\d+)}:([^$]+)\\$");
    final Matcher matcher = compile.matcher(declaration);
    if(matcher.find()) {
      length = Integer.parseInt(matcher.group(1));
      answer = matcher.group(2);
    } else {
      throw new ParserException("Could not decode fill-in question");
    }
  }
}
