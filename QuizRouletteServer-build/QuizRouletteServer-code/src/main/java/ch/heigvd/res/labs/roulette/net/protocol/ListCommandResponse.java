package ch.heigvd.res.labs.roulette.net.protocol;

import java.util.List;
import java.util.List;

import ch.heigvd.res.labs.roulette.data.Student;

/**
 * This class represents the response sent after the BYE
 * command for the v2 implementation
 *
 * @author Valentin Finini
 */
public class ListCommandResponse {

  private List<Student> students;

  public ListCommandResponse()
  {}

  public ListCommandResponse(List<Student> students) {
    this.students = students;
  }

  public List<Student> getStudents() {
    return students;
  }

  public void setStudents(List<Student> students) {
    this.students = students;
  }

}
