package org.liquiz.canvas;

public class CanvasQuizAnswer {
  // The unique identifier for the answer.  Do not supply if this answer is part
  // of a new question
  public long id;
  // The text of the answer.
  public String answer_text;
  // An integer to determine correctness of the answer. Incorrect answers should
  // be 0, correct answers should be non-negative.
  public double answer_weight;
  // Specific contextual comments for a particular answer.
  public String answer_comments;
  // Used in missing word questions.  The text to follow the missing word
  public String text_after_answers;
  // Used in matching questions.  The static value of the answer that will be
  // displayed on the left for students to match for.
  public String answer_match_left;
  // Used in matching questions. The correct match for the value given in
  // answer_match_left.  Will be displayed in a dropdown with the other
  // answer_match_right values..
  public String answer_match_right;
  // Used in matching questions. A list of distractors, delimited by new lines (
  // ) that will be seeded with all the answer_match_right values.
  public String matching_answer_incorrect_matches;
  // Used in numerical questions.  Values can be 'exact_answer', 'range_answer',
  // or 'precision_answer'.
  public String numerical_answer_type;
  // Used in numerical questions of type 'exact_answer'.  The value the answer
  // should equal.
  public int exact;
  // Used in numerical questions of type 'exact_answer'. The margin of error
  // allowed for the student's answer.
  public int margin;
  // Used in numerical questions of type 'precision_answer'.  The value the answer
  // should equal.
  public double approximate;
  // Used in numerical questions of type 'precision_answer'. The numerical
  // precision that will be used when comparing the student's answer.
  public int precision;
  // Used in numerical questions of type 'range_answer'. The start of the allowed
  // range (inclusive).
  public int start;
  // Used in numerical questions of type 'range_answer'. The end of the allowed
  // range (inclusive).
  public int end;
  // Used in fill in multiple blank and multiple dropdowns questions.
  public String blank_id;

  @Override
  public String toString() {
    return "CanvasQuizAnswer{" +
        "answer_text='" + answer_text + '\'' +
        ", answer_weight=" + answer_weight +
        '}';
  }
}
