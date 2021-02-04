package org.liquiz.parser;

public class QuestionDefinition {
  public final String style;
  public final String name;
  public final String values;
  public final String points;

  public QuestionDefinition(String style, String name, String values, String points) {
    this.style = style;
    this.name = name;
    this.values = values;
    this.points = points;
  }
}
