import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Definition {
  public final List<String> values =  new ArrayList<>();

  public Definition(String[] values) {
    this.values.addAll(Arrays.asList(values));
  }
}
