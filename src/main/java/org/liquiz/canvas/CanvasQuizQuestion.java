package org.liquiz.canvas;

import java.util.ArrayList;
import java.util.List;

public class CanvasQuizQuestion {
  public long id;
  public long quiz_id;
  // The order in which the question will be retrieved and displayed.
  public int position;
  public String question_name;
  public QuestionType question_type;
  public String question_text;
  public int points_possible;
  public String correct_comments;
  public String incorrect_comments;
  public String neutral_comments;
  public List<CanvasQuizAnswer> answers = new ArrayList<>();
}
