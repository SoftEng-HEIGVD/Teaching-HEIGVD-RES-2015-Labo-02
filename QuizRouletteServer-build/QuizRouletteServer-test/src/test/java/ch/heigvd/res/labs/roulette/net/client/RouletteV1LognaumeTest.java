package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * This class contains more tests to test the client
 * implementation of the Roulette Protocol (version 1)
 * 
 * @author Guillaume Milani
 */
public class RouletteV1LognaumeTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();
  
  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV1Protocol.VERSION);

  @Test
  @TestAuthor(githubId = "lognaume")
  public void theServerShouldHaveAStudentAfterLoad() throws IOException {
    int port = roulettePair.getServer().getPort();
    IRouletteV1Client client = new RouletteV1ClientImpl();
    client.connect("localhost", port);
    client.loadStudent("John Doe");
    int numberStudents = client.getNumberOfStudents();
    assertEquals(1, numberStudents);
  }
  
  @Test
  @TestAuthor(githubId = "lognaume")
  public void theServerShouldHaveATheGoodStudentAfterLoad() throws IOException, EmptyStoreException {
    int port = roulettePair.getServer().getPort();
    IRouletteV1Client client = new RouletteV1ClientImpl();
    client.connect("localhost", port);
    String fullname = "John Doe";
    client.loadStudent(fullname);
    Student expected = new Student(fullname);
    Student student = client.pickRandomStudent();
    assertEquals(student, expected);
  }  
}
