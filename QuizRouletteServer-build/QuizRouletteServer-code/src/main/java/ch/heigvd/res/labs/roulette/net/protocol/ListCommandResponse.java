/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.protocol;

import ch.heigvd.res.labs.roulette.data.Student;
import java.util.List;

/**
 *
 * @author Gabriel
 */
public class ListCommandResponse {
    
    private List<Student> students;
    
    public ListCommandResponse() {
    }
    
    public ListCommandResponse(List<Student> students) {
        this.students = students;
    }
}
