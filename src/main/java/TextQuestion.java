import java.util.ArrayList;
import java.util.List;

public class TextQuestion implements Question {
  public final String name;
  public final int points;
  public final List<QuestionElement> elements = new ArrayList<>();

  public TextQuestion(String name, int points) {
    this.name = name;
    this.points = points;
  }

  @Override
  public void addElement(QuestionElement qe){
    this.elements.add(qe);
  }

  @Override
  public void addElement(String text) {
    this.addElement(new TextElement(text));
  }

}
