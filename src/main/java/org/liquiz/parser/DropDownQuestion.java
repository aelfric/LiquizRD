package org.liquiz.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DropDownQuestion implements QuestionElement {
  public final List<String> choices;
  public String answer;

  public DropDownQuestion(String sequence) {
    this.choices = new ArrayList<>();

    final Pattern compile = Pattern.compile("\\$dro:([^$]+)\\$");
    final Matcher matcher = compile.matcher(sequence);
    if(matcher.find()) {
      final String[] values = matcher.group(1).split(",");
      for (String value : values) {
        if (value.startsWith("*")) {
          this.choices.add(value.substring(1));
          this.answer = value.substring(1);
        } else {
          this.choices.add(value);
        }
      }
    }
  }
}
