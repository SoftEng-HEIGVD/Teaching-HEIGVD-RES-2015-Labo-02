package ch.heigvd.res.labs.roulette.net.protocol;

import ch.heigvd.res.labs.roulette.data.Student;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "LOAD" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class. 
 * 
 * There is one subtelty: depending on the outcome, the payload will EITHER 
 * contain an "error" attribute, OR a "fullname" attribute. The @JsonInclude 
 * annotation is used to handle this issue.
 * 
 * @author abass mahdavi 
 * almost a copy past of RandomCommandResponse.java proposed by Pr Liechti
 */


@JsonInclude(Include.NON_NULL)
public class ListCommandResponse {
    
  private List<Student> students;

  public ListCommandResponse(List<Student> students) {
    this.students = students;
  }


  public List<Student> getStudents() {
    ArrayList<Student> list = new ArrayList<>();
    list.addAll(students);
    return list;
  }


}
