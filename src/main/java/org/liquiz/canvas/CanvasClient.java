package org.liquiz.canvas;

import com.google.gson.Gson;
import org.liquiz.parser.*;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class CanvasClient {
  public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

    Properties prop = new Properties();
    String propFileName = "config.properties";

    InputStream inputStream = CanvasClient.class.getClassLoader().getResourceAsStream(propFileName);

    if (inputStream != null) {
      prop.load(inputStream);
    } else {
      throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
    }

    InputStream resourceAsStream = CanvasClient.class
        .getClassLoader()
        .getResourceAsStream("data-types.lq");

    String text = new BufferedReader(
        new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));
    Parser parser = new Parser();
    List<Token> tokens = parser.tokenize(text);
    final Quiz quiz = parser.parse(tokens);

    for (Question question : quiz.questions) {
      final TextQuestion textQuestion = (TextQuestion) question;

      final CanvasQuizQuestion canvasQuizQuestion = new CanvasQuizQuestion();
      canvasQuizQuestion.quiz_id = 52532;
      canvasQuizQuestion.question_text = textQuestion.questionText;
      canvasQuizQuestion.question_type = "multiple_answers_question";
      canvasQuizQuestion.points_possible = textQuestion.points;

      final PredefinedMultipleChoice element = (PredefinedMultipleChoice) textQuestion.elements.get(0);
      for (String choice : element.choices) {
        final CanvasQuizAnswer answer1 = new CanvasQuizAnswer();
        answer1.answer_text = choice;
        answer1.answer_weight = element.answers.contains(choice) ?
            1.0 / element.answers.size() :
            0.0;
        canvasQuizQuestion.answers.add(answer1);
      }

      final Map<String, CanvasQuizQuestion> map = Map.of("question", canvasQuizQuestion);

      final Gson gson = new Gson();
      final String accessToken = prop.getProperty("access_token");
      final HttpRequest httpRequest = HttpRequest.newBuilder(new URI("https://sit.instructure.com//api/v1/courses/45047/quizzes/52532/questions"))
          .header("Authorization", "Bearer " + accessToken)
          .header("Accept", "application/json")
          .header("Content-Type", "application/json")
//          .GET()
          .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(map)))
          .build();

      HttpResponse<String> response = HttpClient.newBuilder()
          .build()
          .send(httpRequest, HttpResponse.BodyHandlers.ofString());

      System.out.println(response.body());
    }
  }
}
