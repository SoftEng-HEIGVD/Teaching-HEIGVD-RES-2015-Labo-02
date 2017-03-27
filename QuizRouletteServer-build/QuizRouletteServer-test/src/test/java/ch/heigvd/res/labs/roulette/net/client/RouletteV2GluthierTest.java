package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.Test;
import org.junit.Ignore;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Olivier Liechti
 */
@Ignore
public class RouletteV2GluthierTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "gluthier")
    public void theDefaultServerPortShouldBe2613() {
        Assert.assertEquals(2613, roulettePair.getServer().getPort());
    }
    
    @Test
    @TestAuthor(githubId = "gluthier")
    public void theServerShouldHaveZeroStudentAfterAClearCommand() throws IOException {
        RouletteV2ClientImpl client = (RouletteV2ClientImpl) roulettePair.getClient();
        assertEquals(0, client.getNumberOfStudents());
        client.loadStudent("sacha");
        client.loadStudent("olivier");
        client.loadStudent("fabienne");
        assertEquals(3, client.getNumberOfStudents());
        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
    }
    
    @Test
    @TestAuthor(githubId = "gluthier")
    public void theServerShouldListNoStudentAtStart() throws IOException {
        RouletteV2ClientImpl client = (RouletteV2ClientImpl) roulettePair.getClient();
        ArrayList<Student> res = (ArrayList)client.listStudents();
        assertEquals(0, res.size());
    }
    
    @Test
    @TestAuthor(githubId = "gluthier")
    public void theServerShouldListTheStudents() throws IOException {
        ArrayList<Student> students = new ArrayList();
        students.add(new Student("sacha"));
        students.add(new Student("olivier"));
        students.add(new Student("fabienne"));
        RouletteV2ClientImpl client = (RouletteV2ClientImpl) roulettePair.getClient();
        ArrayList<Student> res = (ArrayList)client.listStudents();
        assertEquals(students, res);
    }
}
