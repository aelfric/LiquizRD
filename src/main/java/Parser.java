import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedList;
import java.util.List;

public class Parser {
  final Tokenizer tokenizer;
  LinkedList<Token> tokens;
  Token lookahead;

  StringBuilder textLiteral = new StringBuilder();
  Question currentQuestion;

  Quiz quiz;

  Gson gson = new GsonBuilder().setPrettyPrinting().create();

  Parser() {
    this.tokenizer = new Tokenizer();

    tokenizer.add("\\s+", Token.WHITESPACE);
    tokenizer.add("\\{\"quizspec[^\\}]*\\}\n", Token.QUIZ_SPEC);
    tokenizer.add("\\{\"style[^\\}]*\\}\n", Token.QUESTION);
    tokenizer.add("---\n", Token.DELIMITER);
    tokenizer.add("\\$mch:[^\\$]+\\$", Token.MULTI_CHOICE_HORIZONTAL);   //    {"mch", new MultipleChoiceHorizontal()},
    tokenizer.add("\\$mcv:[^\\$]+\\$", Token.MULTI_CHOICE_VERTICAL);   //    {"mcv", new MultipleChoiceVertical()},
    tokenizer.add("\\$mah:[^\\$]+\\$", Token.MULTI_ANSWER_HORIZONTAL);   //    {"mah", new MultipleAnswerHorizontal()},
    tokenizer.add("\\$mav:[^\\$]+\\$", Token.MULTI_ANSWER_VERTICAL);   //    {"mav", new MultipleAnswerVertical()},
    tokenizer.add("\\$f[^\\$]+\\$", Token.FILL_IN);   //    {"f", new FillIn()},
    tokenizer.add("\\$tar:[^\\$]+\\$", Token.TEXT_QUESTION);   //    {"tar", new TextQuestion()},
    tokenizer.add("\\$def:[^\\$]+\\$", Token.DEFINITION);   //    {"def", new Definition()},
    tokenizer.add("\\$dro:[^\\$]+\\$", Token.DROP_DOWN);   //    {"dro", new DropDownQuestion()},
    tokenizer.add("\\$img:[^\\$]+\\$", Token.IMAGE);   //    {"img", new Image()},
    tokenizer.add("\\$vid:[^\\$]+\\$", Token.VIDEO);   //    {"vid", new Video()},
    tokenizer.add("\\$rnd:[^\\$]+\\$", Token.RANDOM_VAR);   //    {"rnd", new RandomVar()},
    tokenizer.add("\\$var:[^\\$]+\\$", Token.VARIABLE);   //    {"var", new Variable()},
    tokenizer.add("\\$for:[^\\$]+\\$", Token.FORMULA_QUESTION);   //    {"for", new FormulaQuestion()},
    tokenizer.add("\\$mat:[^\\$]+\\$", Token.MATRIX_QUESTION);   //    {"mat", new MatrixQuestion()}
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

  public void parse(List<Token> tokens) {
    this.tokens = new LinkedList<>(tokens);
    lookahead = this.tokens.get(0);

    try {
      quiz();
    } catch (Exception e){
      throw new ParserException("Unexpected symbol %s", lookahead);
    }

    if (lookahead.token != -1)
      throw new ParserException("Unexpected symbol %s found", lookahead);

    System.out.println(gson.toJson(quiz));
  }

  private void quiz() {
    quiz = new Quiz();
    quizDefinition();
    question();
  }

  private void question() {
    if(currentQuestion != null) {
      quiz.addQuestion(currentQuestion);
      currentQuestion = null;
    }
    if (this.lookahead.token == Token.QUESTION) {
      final QuestionDefinition qDef = gson.fromJson(this.lookahead.sequence, QuestionDefinition.class);
      switch (qDef.style) {
        case "def":
          quiz.addDefinition(
              qDef.name,
              new Definition(qDef.values.split(","))
          );
          break;
        case "text":
        default:
          this.currentQuestion = new TextQuestion(
              qDef.name,
              Integer.parseInt(qDef.points)
          );
      }
      if (this.currentQuestion != null) {
        this.currentQuestion.addElement(
            this.textLiteral.toString()
        );
        textLiteral = new StringBuilder();
      }
      nextToken();
      text();
      input();
      delimiter();
    }
  }

  private void input() {
    switch (lookahead.token) {
      case Token.MULTI_CHOICE_HORIZONTAL:
        this.currentQuestion.addElement(
            new MultipleChoiceQuestion(
                "horizontal",
                lookahead.sequence
            )
        );
        break;
      case Token.MULTI_CHOICE_VERTICAL:
        this.currentQuestion.addElement(
            new MultipleChoiceQuestion(
                "vertical",
                lookahead.sequence
            )
        );
        break;
      case Token.MULTI_ANSWER_HORIZONTAL:
        this.currentQuestion.addElement(
            new MultipleChoiceQuestion(
                "horizonal",
                lookahead.sequence
            )
        );
        break;
      case Token.MULTI_ANSWER_VERTICAL:
        this.currentQuestion.addElement(
            new MultipleChoiceQuestion(
                "vertical",
                lookahead.sequence
            )
        );
        break;
      case Token.FILL_IN:
        this.currentQuestion.addElement(
            new FillInQuestion(lookahead.sequence)
        );
      case Token.TEXT_QUESTION:
      case Token.DEFINITION:
      case Token.DROP_DOWN:
      case Token.IMAGE:
      case Token.VIDEO:
      case Token.RANDOM_VAR:
      case Token.VARIABLE:
      case Token.FORMULA_QUESTION:
      case Token.MATRIX_QUESTION:
        // todo - implement me
    }
    nextToken();
    whitespace();
    text();
    input();
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
      text();
    }
  }

  private void delimiter() {
    if (this.lookahead.token == Token.DELIMITER) {
      nextToken();
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
