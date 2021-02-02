import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Quiz {
  Map<String, Definition> definitions = new HashMap<>();
  List<Question> questions = new ArrayList<>();
  private String name;

  public void addQuestion(Question question){
    questions.add(question);
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void addDefinition(String name, Definition definition) {
    this.definitions.put(name, definition);
  }
}
