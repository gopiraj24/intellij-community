// "Replace with collect" "true"
import java.util.*;
import java.util.stream.Collectors;

public class Collect {
  class Person {
    String getName() {
      return "";
    }
  }

  void collectNames(List<Person> persons){
    Set<String> names = new HashSet<>();
    for (Person person : pers<caret>ons) {
      names.add(person.getName());
    }
  }
}
