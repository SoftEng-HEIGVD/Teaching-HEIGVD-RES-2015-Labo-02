package ch.heigvd.res.labs.roulette.net.protocol;

import ch.heigvd.res.labs.roulette.data.Student;

import java.util.List;

/**
 * @author Gaëtan Othenin-Girard & Xavier Vaz Afonso
 */
public class ListCommandResponse {
    private List<Student> students;

    public ListCommandResponse(List<Student> listOfStudents) {
        students = listOfStudents;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setListOfStudents(List<Student> listOfStudents) {
        students = listOfStudents;
    }
}
