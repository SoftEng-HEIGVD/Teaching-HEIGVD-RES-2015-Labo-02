package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import org.junit.Test;
import static org.junit.Assert.*;

import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

public class RouletteV1ZorukTest {
    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV1Protocol.VERSION);
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Test
    @TestAuthor(githubId = "Zoruk")
    public void theServerShouldBeRunning() {
        assertTrue(roulettePair.getServer().isRunning());
    }
    
    @Test
    @TestAuthor(githubId = "Zoruk")
    public void theTheClientShouldBeConnected() {
        assertTrue(roulettePair.getClient().isConnected());
    }
    
    @Test
    @TestAuthor(githubId = "Zoruk")
    public void weShouldBeAbleToLoadOneStudent() throws IOException {
        roulettePair.getClient().loadStudent("Loïc Haas");
        assertTrue(roulettePair.getClient().getNumberOfStudents() == 1);
    }
    
    @Test
    @TestAuthor(githubId = "Zoruk")
    public void weShouldBeAbleToLoadMultipleStudents() throws IOException {
        List<Student> students = new LinkedList<>();
        
        students.add(new Student("Loïc Haas"));
        students.add(new Student("James Nolan"));
        students.add(new Student("Stéphan Donnet"));
        students.add(new Student("Thibault Schowing"));
        
        roulettePair.getClient().loadStudents(students);
    }
    
    @Test
    @TestAuthor(githubId = "Zoruk")
    public void weShouldBeAbleTopickRandomStudent() throws IOException {
        weShouldBeAbleToLoadMultipleStudents(); // Add some students
        try {
            Student student = roulettePair.getClient().pickRandomStudent();
        } catch (EmptyStoreException ex) {
            Logger.getLogger(RouletteV1ZorukTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    @TestAuthor(githubId = "Zoruk")
    public void thePickRandomFunctionSouldThrowEmptyStoreException() throws IOException, EmptyStoreException {
        exception.expect(EmptyStackException.class);
        roulettePair.getClient().pickRandomStudent();
    }
    
    @Test
    @TestAuthor(githubId = "Zoruk")
    public void theNumberOfStudentShouldBeCorrect() throws IOException {
        assertEquals(roulettePair.getClient().getNumberOfStudents(), 0);
        
        roulettePair.getClient().loadStudent("Loïc Haas");
        assertEquals(roulettePair.getClient().getNumberOfStudents(), 1);
        
        roulettePair.getClient().loadStudent("Super Student");
        assertEquals(roulettePair.getClient().getNumberOfStudents(), 2);
       
        List<Student> students = new LinkedList<>();
        
        students.add(new Student("James Nolan"));
        students.add(new Student("Stéphan Donnet"));
        students.add(new Student("Thibault Schowing"));
        
        roulettePair.getClient().loadStudents(students);
        
        assertEquals(roulettePair.getClient().getNumberOfStudents(), 5);
    }
}
