public class Token {
  public static final int EPSILON = -1;
  static final int WHITESPACE = 0;
  static final int QUIZ_SPEC = 1;
  static final int QUESTION = 2;
  static final int DELIMITER = 3;
  static final int MULTI_CHOICE_HORIZONTAL = 4;
  static final int MULTI_CHOICE_VERTICAL = 5;
  static final int MULTI_ANSWER_HORIZONTAL = 6;
  static final int MULTI_ANSWER_VERTICAL = 7;
  static final int FILL_IN = 8;
  static final int TEXT_QUESTION = 9;
  static final int DEFINITION = 10;
  static final int DROP_DOWN = 11;
  static final int IMAGE = 12;
  static final int VIDEO = 13;
  static final int RANDOM_VAR = 14;
  static final int VARIABLE = 15;
  static final int FORMULA_QUESTION = 16;
  static final int MATRIX_QUESTION = 17;
  static final int WORD = 18;
  public static final int AUTHOR = 19;
  public final int token;
  public final String sequence;



  public Token(int token, String sequence) {
    super();
    this.token = token;
    this.sequence = sequence;
  }

  @Override
  public String toString() {
    return "(" +
        "token=" + token +
        ", sequence='" + sequence + '\'' +
        ')';
  }
}