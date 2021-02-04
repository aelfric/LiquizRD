package org.liquiz.parser;

public class TextElement implements QuestionElement {
  public final String text;

  public TextElement(String text) {
    this.text = text;
  }
}
