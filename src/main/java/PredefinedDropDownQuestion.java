import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PredefinedDropDownQuestion implements QuestionElement {
  public final List<String> choices;
  public String answer;

  public PredefinedDropDownQuestion(
      String sequence,
      Map<String, Definition> definitions) {

    final Pattern compile = Pattern.compile("\\$def:([^:]+):([^$]+)\\$");
    final Matcher matcher = compile.matcher(sequence);
    if (matcher.find()) {
      this.choices = definitions.get(matcher.group(1)).values;
      this.answer = matcher.group(2);
    } else {
      throw new ParserException("Unknown definitions " + sequence);
    }
  }
}
