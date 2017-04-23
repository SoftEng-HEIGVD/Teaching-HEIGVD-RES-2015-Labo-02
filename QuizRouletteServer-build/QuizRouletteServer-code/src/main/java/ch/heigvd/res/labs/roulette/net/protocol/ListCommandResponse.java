package ch.heigvd.res.labs.roulette.net.protocol;

import ch.heigvd.res.labs.roulette.data.Student;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "INFO" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @author Olivier Liechti
 */
public class ListCommandResponse {

    private List<Student> students  = new ArrayList<>();

    public ListCommandResponse(List<Student> students) {
        this.students = students;
    }
    
    public ListCommandResponse() {
    }

    /**
     * @return the students
     */
    public List<Student> getStudents() {
        return students;
    }

    /**
     * @param students the students to set
     */
    public void setStudents(List<Student> students) {
        this.students = students;
    }
    
    public void addStudent(Student student) {
        students.add(student);
    }
}
