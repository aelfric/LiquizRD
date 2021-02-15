package org.liquiz.parser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultipleChoiceQuestion extends ChoiceQuestion{
  public final String style;
  public final boolean isMultiAnswer;

  public MultipleChoiceQuestion(String style, String sequence, boolean isMultiAnswer) {
    this.style = style;
    this.choices = new ArrayList<>();
    this.answers = new ArrayList<>();
    this.isMultiAnswer = isMultiAnswer;

    final Pattern compile = Pattern.compile("\\$[a-z]+:([^$]+)\\$");
    final Matcher matcher = compile.matcher(sequence);
    if(matcher.find()) {
      final String[] values = matcher.group(1).split(",");
      for (String value : values) {
        if (value.startsWith("*")) {
          this.choices.add(value.substring(1));
          this.answers.add(value.substring(1));
        } else {
          this.choices.add(value);
        }
      }
    }
  }
}
