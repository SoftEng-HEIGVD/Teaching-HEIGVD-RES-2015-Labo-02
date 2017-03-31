package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 * These tests are partly based on the tests made for the V1 protocol
 *
 * As these tests will be used by every student, we could not test things that
 * are not precisely written in the specification (for exemple, do we have to
 * use a "ByeCommandResponse" ?)
 *
 * These tests have been modified to test our new implementation of the server
 *
 * @version 0.2
 * @author Remi, Aurelie
 */
public class RouletteV2remij1Test {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "wasadigi")
    public void theTestRouletteServerShouldRunDuringTests() throws IOException {
        assertTrue(roulettePair.getServer().isRunning());
    }

    @Test
    @TestAuthor(githubId = "wasadigi")
    public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
        assertTrue(roulettePair.getClient().isConnected());
    }

    @Test
    @TestAuthor(githubId = {"wasadigi", "remij1", "aurelielevy"})
    public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        assertFalse(client.isConnected());
        client.connect("localhost", port);
        assertTrue(client.isConnected());
    }

    @Test
    @TestAuthor(githubId = {"wasadigi", "remij1", "aurelielevy"})
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

    @Test
    @TestAuthor(githubId = {"wasadigi", "remij1", "aurelielevy"})
    public void theServerShouldHaveZeroStudentsAtStart() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        client.connect("localhost", port);
        int numberOfStudents = client.getNumberOfStudents();
        assertEquals(0, numberOfStudents);
    }

    @Test
    @TestAuthor(githubId = {"wasadigi", "SoftEng-HEIGVD"})
    public void theServerShouldStillHaveZeroStudentsAtStart() throws IOException {
        assertEquals(0, roulettePair.getClient().getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = {"SoftEng-HEIGVD", "remij1", "aurelielevy"})
    public void theServerShouldCountStudents() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        assertEquals(0, client.getNumberOfStudents());
        client.loadStudent("sacha");
        assertEquals(1, client.getNumberOfStudents());
        client.loadStudent("olivier");
        assertEquals(2, client.getNumberOfStudents());
        client.loadStudent("fabienne");
        assertEquals(3, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = {"wasadigi", "remij1", "aurelielevy"})
    public void theServerShouldSendAnErrorResponseWhenRandomIsCalledAndThereIsNoStudent() throws IOException, EmptyStoreException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        exception.expect(EmptyStoreException.class);
        client.pickRandomStudent();
    }

    //------- Specifically for the V2 protocol
    @Test
    @TestAuthor(githubId = {"remij1", "aurelielevy"})
    public void theServerShouldClearData() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        assertEquals(0, client.getNumberOfStudents());
        client.loadStudent("sacha");
        assertEquals(1, client.getNumberOfStudents());
        client.loadStudent("olivier");
        assertEquals(2, client.getNumberOfStudents());
        client.loadStudent("fabienne");
        assertEquals(3, client.getNumberOfStudents());

        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = {"remij1", "aurelielevy"})
    public void theServerShouldListData() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        assertEquals(0, client.getNumberOfStudents());
        client.loadStudent("sacha");
        assertEquals(1, client.getNumberOfStudents());
        client.loadStudent("olivier");
        assertEquals(2, client.getNumberOfStudents());
        client.loadStudent("fabienne");
        assertEquals(3, client.getNumberOfStudents());

        List<Student> students = client.listStudents();
        assertEquals("sacha", students.get(0).getFullname());
        assertEquals("olivier", students.get(1).getFullname());
        assertEquals("fabienne", students.get(2).getFullname());
    }

    @Test
    @TestAuthor(githubId = {"remij1", "aurelielevy"})
    public void theDefaultPortShouldBe2613() {
        assertEquals(RouletteV2Protocol.DEFAULT_PORT, 2613);

        /* Note : the port really used by the server could not be tests,
         * because it is possible that it's already used.
         */
    }

    @Test
    @TestAuthor(githubId = {"remij1", "aurelielevy"})
    public void theServerShouldReturnAnExceptionForTheNumberOfCommandsDone() throws IOException, Exception {
        RouletteV2ClientImpl client = (RouletteV2ClientImpl) roulettePair.getClient();

        client.loadStudent("Marie");
        client.listStudents();

        exception.expect(Exception.class);
        client.getNumberOfCommands();
    }

    @Test
    @TestAuthor(githubId = {"remij1", "aurelielevy"})
    public void theServerShouldReturnTheNumberOfCommand() throws IOException, Exception {
        RouletteV2ClientImpl client = (RouletteV2ClientImpl) roulettePair.getClient();

        client.loadStudent("Marie");
        client.listStudents();

        client.disconnect();

        int nbOfCommands = client.getNumberOfCommands();
        client.connect("localhost", roulettePair.getServer().getPort());
        
        //Disconnecting is a command
        assertEquals(3, nbOfCommands);
    }

    @Test
    @TestAuthor(githubId = {"remij1", "aurelielevy"})
    public void theServerShouldReturnTheNumberOfAddedStudents() throws IOException {
        RouletteV2ClientImpl client = (RouletteV2ClientImpl) roulettePair.getClient();

        client.loadStudent("Marie");
        assertEquals(1, client.getNumberOfStudentsAdded());
        
        LinkedList<Student> students = new LinkedList<>();
        students.add(new Student("Marie"));
        students.add(new Student("Jacques"));
        
        client.loadStudents(students);
        assertEquals(2, client.getNumberOfStudentsAdded());
    }
    
    
}
