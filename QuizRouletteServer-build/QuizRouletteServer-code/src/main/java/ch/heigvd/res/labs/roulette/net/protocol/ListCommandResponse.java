/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.protocol;

import ch.heigvd.res.labs.roulette.data.Student;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Zundler Cyrill & Wertenbroek Rick
 */
public class ListCommandResponse {

    private List<Student> students = new LinkedList<>();

    public ListCommandResponse() {
    }

    public ListCommandResponse(List<Student> students) {
        this.students = students;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public void addStudent(Student student) {
        students.add(student);
    }

}
