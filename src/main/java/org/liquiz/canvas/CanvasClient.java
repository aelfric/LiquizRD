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
    final Quiz quiz = parser.parse(text);
    final int quizId = Integer.parseInt(prop.getProperty("quiz_id"));
    final String accessToken = prop.getProperty("access_token");
    final String courseId = prop.getProperty("course_id");
    final boolean dryRun = Boolean.parseBoolean(prop.getProperty("dry_run"));

    for (Question question : quiz.questions) {
      final CanvasQuizQuestion canvasQuizQuestion = CanvasQuizQuestion.fromParsedQuestion(quizId, (TextQuestion) question);

      final Map<String, CanvasQuizQuestion> map = Map.of("question", canvasQuizQuestion);

      final Gson gson = new Gson();
      final HttpRequest httpRequest = HttpRequest.newBuilder(
          new URI(
              String.format("https://sit.instructure.com//api/v1/courses/%s/quizzes/%d/questions", courseId, quizId)))
          .header("Authorization", "Bearer " + accessToken)
          .header("Accept", "application/json")
          .header("Content-Type", "application/json")
//          .GET()
          .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(map)))
          .build();

      if (!dryRun) {
        HttpResponse<String> response = HttpClient.newBuilder()
            .build()
            .send(httpRequest, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
      }
    }
  }

}
