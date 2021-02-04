package org.liquiz.parser;

import java.util.ArrayList;
import java.util.List;

public class TextQuestion implements Question {
  public final String name;
  public final int points;
  public final List<QuestionElement> elements = new ArrayList<>();
  public String author;
  public final String style;
  public String questionText;

  public TextQuestion(String name, int points, String text) {
    this.name = name;
    this.points = points;
    this.style = text;
  }

  @Override
  public void addElement(QuestionElement qe){
    this.elements.add(qe);
  }

  @Override
  public void addElement(String text) {
    if (this.questionText == null && this.elements.isEmpty()){
      this.questionText = text;
    } else {
      this.addElement(new TextElement(text));
    }
  }

  @Override
  public void setAuthor(String sequence) {
    final String[] split = sequence.split(":");
    this.author = split[1].trim();
  }

}
