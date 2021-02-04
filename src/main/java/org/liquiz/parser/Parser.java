package org.liquiz.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Parser {
  final Tokenizer tokenizer;
  LinkedList<Token> tokens;
  Token lookahead;

  StringBuilder textLiteral = new StringBuilder();
  Question currentQuestion;

  Quiz quiz;

  Gson gson = new GsonBuilder().setPrettyPrinting().create();

  public Parser() {
    this.tokenizer = new Tokenizer();

    tokenizer.add("\\s+", Token.WHITESPACE);
    tokenizer.add("\\{\"quizspec[^\\}]*\\}\n", Token.QUIZ_SPEC);
    tokenizer.add("\\{\"style[^\\}]*\\}\n", Token.QUESTION);
    tokenizer.add("---\n", Token.DELIMITER);
    tokenizer.add("\\$mch:[^\\$]*\\$", Token.MULTI_CHOICE_HORIZONTAL);   //    {"mch", new MultipleChoiceHorizontal()},
    tokenizer.add("\\$mcv:[^\\$]*\\$", Token.MULTI_CHOICE_VERTICAL);   //    {"mcv", new MultipleChoiceVertical()},
    tokenizer.add("\\$mah:[^\\$]*\\$", Token.MULTI_ANSWER_HORIZONTAL);   //    {"mah", new MultipleAnswerHorizontal()},
    tokenizer.add("\\$mav:[^\\$]*\\$", Token.MULTI_ANSWER_VERTICAL);   //    {"mav", new MultipleAnswerVertical()},
    tokenizer.add("\\$f[^\\$]+\\$", Token.FILL_IN);   //    {"f", new FillIn()},
    tokenizer.add("\\$tar:[^\\$]*\\$", Token.TEXT_QUESTION);   //    {"tar", new org.liquiz.parser.TextQuestion()},
    tokenizer.add("\\$def:[^\\$]*\\$", Token.DEFINITION);   //    {"def", new org.liquiz.parser.Definition()},
    tokenizer.add("\\$dro:[^\\$]*\\$", Token.DROP_DOWN);   //    {"dro", new org.liquiz.parser.DropDownQuestion()},
    tokenizer.add("\\$img:[^\\$]*\\$", Token.IMAGE);   //    {"img", new Image()},
    tokenizer.add("\\$vid:[^\\$]*\\$", Token.VIDEO);   //    {"vid", new Video()},
    tokenizer.add("\\$rnd:[^\\$]*\\$", Token.RANDOM_VAR);   //    {"rnd", new RandomVar()},
    tokenizer.add("\\$var:[^\\$]*\\$", Token.VARIABLE);   //    {"var", new Variable()},
    tokenizer.add("\\$for:[^\\$]*\\$", Token.FORMULA_QUESTION);   //    {"for", new FormulaQuestion()},
    tokenizer.add("\\$mat:[^\\$]*\\$", Token.MATRIX_QUESTION);   //    {"mat", new MatrixQuestion()}
    tokenizer.add("^author:[A-Za-z ]+\n", Token.AUTHOR);   //    {"mat", new MatrixQuestion()}
    tokenizer.add("[^\\s]+", Token.WORD);
  }

  private void nextToken() {
    tokens.pop();
    // at the end of input we return an epsilon token
    if (tokens.isEmpty())
      lookahead = new Token(Token.EPSILON, "");
    else
      lookahead = tokens.get(0);

//    System.out.println(lookahead);
  }

  public List<Token> tokenize(String str) {
    return tokenizer.tokenize(str);
  }

  public Quiz parse(List<Token> tokens) {
    this.tokens = new LinkedList<>(tokens);
    lookahead = this.tokens.get(0);

    try {
      quiz();
      this.quiz.addQuestion(this.currentQuestion);
    } catch (Exception e) {
      throw new ParserException(lookahead, e);
    }

    if (lookahead.token != -1)
      throw new ParserException(lookahead);

    System.out.println(gson.toJson(quiz));
    return quiz;
  }

  private void quiz() {
    quiz = new Quiz();
    quizDefinition();
    question();
  }

  private void question() {
    if (this.lookahead.token == Token.QUESTION) {
      final QuestionDefinition qDef = gson.fromJson(this.lookahead.sequence, QuestionDefinition.class);
      if ("def".equals(qDef.style)) {
        quiz.addDefinition(
            qDef.name,
            new Definition(qDef.values.split(","))
        );
      } else {
        this.currentQuestion = new TextQuestion(
            qDef.name,
            Integer.parseInt(qDef.points), qDef.style
        );
      }
      nextToken();
      author();
      element();
      delimiter();
    }
  }

  private void element() {
    if (lookahead.token != Token.WORD && lookahead.token != Token.DELIMITER) {
      this.currentQuestion.addElement(
          this.textLiteral.toString()
      );
      textLiteral = new StringBuilder();
    }
    text();
    input();
  }

  private void author() {
    if (lookahead.token == Token.AUTHOR) {
      this.currentQuestion.setAuthor(lookahead.sequence);
      nextToken();
    }
  }

  private void input() {
    createElement(lookahead)
        .ifPresent((e) -> {
          this.currentQuestion.addElement(e);
          nextToken();
          whitespace();
          element();
        });
  }

  private Optional<QuestionElement> createElement(Token token) {
    return switch (token.token) {
      case Token.MULTI_CHOICE_HORIZONTAL,
          Token.MULTI_ANSWER_HORIZONTAL -> Optional.of(
          new MultipleChoiceQuestion(
              "horizontal",
              token.sequence
          ));
      case Token.MULTI_CHOICE_VERTICAL,
          Token.MULTI_ANSWER_VERTICAL -> Optional.of(
          new MultipleChoiceQuestion(
              "vertical",
              token.sequence
          ));
      case Token.FILL_IN -> Optional.of(new FillInQuestion(token.sequence));
      case Token.TEXT_QUESTION -> Optional.of(new LongTextQuestion(token.sequence));
      case Token.DEFINITION -> Optional.of(
          new PredefinedMultipleChoice(
              token.sequence,
              this.quiz.definitions
          ));
      case Token.DROP_DOWN -> Optional.of(
          new DropDownQuestion(
              token.sequence
          ));
      case Token.IMAGE,
          Token.VIDEO,
          Token.RANDOM_VAR,
          Token.VARIABLE,
          Token.FORMULA_QUESTION,
          Token.MATRIX_QUESTION -> Optional.of(new UnknownElement(token));
      default -> Optional.empty();
    };
  }

  private void text() {
    if (this.lookahead.token == Token.WORD) {
      textLiteral.append(this.lookahead.sequence);
      nextToken();
      whitespace();
    }
  }

  private void whitespace() {
    if (this.lookahead.token == Token.WHITESPACE) {
      textLiteral.append(this.lookahead.sequence);
      nextToken();
      element();
    }
  }

  private void delimiter() {
    if (this.lookahead.token == Token.DELIMITER) {
      nextToken();
      if (currentQuestion != null) {
        quiz.addQuestion(currentQuestion);
        currentQuestion = null;
      }
      question();
    }
  }

  private void quizDefinition() {
    if (lookahead.token == Token.QUIZ_SPEC) {
      final QuizSpec qs = gson.fromJson(lookahead.sequence, QuizSpec.class);
      quiz.setName(qs.name);
      nextToken();
    }
  }
}
