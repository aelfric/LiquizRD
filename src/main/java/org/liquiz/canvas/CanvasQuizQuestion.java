package org.liquiz.canvas;

import org.liquiz.parser.*;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * See https://canvas.instructure.com/doc/api/quiz_questions.html#method.quizzes/quiz_questions.create
 */
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


  static CanvasQuizQuestion fromParsedQuestion(int quizId, TextQuestion question) {

    final CanvasQuizQuestion canvasQuizQuestion = new CanvasQuizQuestion();

    final QuestionType questionType = getQuestionType(question);
    canvasQuizQuestion.quiz_id = quizId;
    canvasQuizQuestion.question_type = questionType;
    canvasQuizQuestion.points_possible = question.points;
    switch (questionType){
      case multiple_choice_question, multiple_answers_question -> {
        canvasQuizQuestion.question_text = question.questionText;

        final ChoiceQuestion element = (ChoiceQuestion) question.elements.get(0);
        for (String choice : element.choices) {
          final CanvasQuizAnswer answer = new CanvasQuizAnswer();
          answer.answer_text = choice;
          answer.answer_weight = element.answers.contains(choice) ?
              100.0 :
              0.0;
          canvasQuizQuestion.answers.add(answer);
        }
      }
      case essay_question -> canvasQuizQuestion.question_text = question.questionText;
      case fill_in_multiple_blanks_question -> {
        int i = 1;
        final StringBuilder questionText = new StringBuilder();
        questionText.append(question.questionText);
        for (QuestionElement element : question.elements) {
          if(element instanceof TextElement textElement){
            questionText.append(textElement.text);
          } else if (element instanceof FillInQuestion fillInQuestion){
            final String blankId = String.format("[blank_%d]", i++);
            questionText.append(blankId);
            final CanvasQuizAnswer answer = new CanvasQuizAnswer();
            answer.answer_text = fillInQuestion.answer;
            answer.blank_id = blankId;
            canvasQuizQuestion.answers.add(answer);
          }
        }
        canvasQuizQuestion.question_text = questionText.toString();
        for (CanvasQuizAnswer answer : canvasQuizQuestion.answers) {
          answer.answer_weight = 100.0;
        }
      }
      default -> throw new IllegalStateException("Unexpected value: " + questionType);
    }
    return canvasQuizQuestion;
  }

  private static QuestionType getQuestionType(TextQuestion textQuestion1) {
    for (QuestionElement element : textQuestion1.elements) {
      if (element instanceof MultipleChoiceQuestion question) {
        if(question.isMultiAnswer){
          return QuestionType.multiple_answers_question;
        } else {
          return QuestionType.multiple_choice_question;
        }
      } else if (element instanceof PredefinedMultipleChoice) {
        return QuestionType.multiple_answers_question;
      } else if (element instanceof LongTextQuestion){
        return QuestionType.essay_question;
      } else {
        if (element instanceof FillInQuestion) {
          return QuestionType.fill_in_multiple_blanks_question;
        }
      }
    }
    throw new RuntimeException("Unknown question type");
  }
}
