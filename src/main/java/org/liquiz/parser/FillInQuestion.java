package org.liquiz.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FillInQuestion implements QuestionElement {
  public final int length;
  public final String answer;

  public FillInQuestion(String declaration) {
    final Pattern compile = Pattern.compile("\\$f(Q\\{\\d+})?:([^$]+)\\$");

    final Matcher matcher = compile.matcher(declaration);
    if(matcher.find()) {
      length = matcher.group(1) == null ? 6 : decodeLength(matcher.group(1));
      answer = matcher.group(2);
    } else {
      throw new ParserException("Could not decode fill-in question");
    }
  }

  private int decodeLength(String str) {
    final Pattern compile = Pattern.compile("Q\\{(\\d+)}");
    final Matcher matcher = compile.matcher(str);
    if(matcher.find()) {
      return Integer.parseInt(matcher.group(1));
    } else {
      return 6;
    }
  }
}
