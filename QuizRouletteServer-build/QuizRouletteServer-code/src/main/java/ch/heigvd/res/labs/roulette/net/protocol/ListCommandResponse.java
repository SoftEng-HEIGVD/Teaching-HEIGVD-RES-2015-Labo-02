package ch.heigvd.res.labs.roulette.net.protocol;

import ch.heigvd.res.labs.roulette.data.Student;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by francoisquellec on 02.04.17.
 */
public class ListCommandResponse {
    List<Student> students;
    public ListCommandResponse(){
        students = new LinkedList<>();
    }
    public ListCommandResponse(List<Student> students){
        this.students = students;
    }
    public List<Student> getStudents(){return students;}
    public void setStudents(List<Student> students){this.students = students;}
}
