package org.liquiz.parser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PredefinedMultipleChoice implements QuestionElement {
  public final List<String> choices;
  public List<String> answers;

  public PredefinedMultipleChoice(
      String sequence,
      Map<String, Definition> definitions) {

    final Pattern compile = Pattern.compile("\\$def:([^:]+):([^$]+)\\$");
    final Matcher matcher = compile.matcher(sequence);
    if (matcher.find()) {
      this.choices = definitions.get(matcher.group(1)).values;
      this.answers = Arrays.asList(matcher.group(2).split(","));
    } else {
      throw new ParserException("Unknown definitions " + sequence);
    }
  }
}
