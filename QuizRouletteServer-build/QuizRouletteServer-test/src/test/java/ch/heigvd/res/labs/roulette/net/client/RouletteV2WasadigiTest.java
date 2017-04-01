package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import static ch.heigvd.res.labs.roulette.data.JsonObjectMapper.toJson;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Olivier Liechti
 * modified by abass mahdavi
 */
public class RouletteV2WasadigiTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerV2Pair roulettePair = new EphemeralClientServerV2Pair(RouletteV2Protocol.VERSION);

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
    @TestAuthor(githubId = "wasadigi")
    public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
        int port = roulettePair.getServer().getPort();
        IRouletteV2Client client = new RouletteV2ClientImpl();
        assertFalse(client.isConnected());
        client.connect("localhost", port);
        assertTrue(client.isConnected());
    }

    @Test
    @TestAuthor(githubId = "wasadigi")
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

    @Test
    @TestAuthor(githubId = "wasadigi")
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
    @TestAuthor(githubId = "SoftEng-HEIGVD")
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
    @TestAuthor(githubId = "wasadigi")
    public void theServerShouldSendAnErrorResponseWhenRandomIsCalledAndThereIsNoStudent() throws IOException, EmptyStoreException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        exception.expect(EmptyStoreException.class);
        client.pickRandomStudent();
    }
    
    
    @Test
    @TestAuthor(githubId = "abass-mahdavi")
    public void theServerShouldReturnCorrectList() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        client.loadStudent("sacha");
        client.loadStudent("olivier");
        client.loadStudent("fabienne");
        List<Student> list = client.listStudents();
        String listString = toJson(list);
        assertEquals(listString,"[{\"fullname\":\"sacha\"},{\"fullname\":\"olivier\"},{\"fullname\":\"fabienne\"}]");
    }
    
    @Test
    @TestAuthor(githubId = "abass-mahdavi")
    public void theServerShouldCleanStudentList() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        client.loadStudent("sacha");
        client.loadStudent("olivier");
        client.loadStudent("fabienne");
        client.clearDataStore();
        List<Student> list = client.listStudents();
        String listString = toJson(list);
        assertEquals(listString,"[]");

    }
    
    @Test
    @TestAuthor(githubId = "abass-mahdavi")
    public void ByeShouldReturnTheNumberOfCommands() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        client.loadStudent("sacha");
        client.loadStudent("olivier");
        client.loadStudent("fabienne");
        client.clearDataStore();        
        String byeMessage = ((RouletteV2ClientImpl)client).getByeMessage();
        assertEquals(byeMessage,"{\"status\":\"success\",\"numberOfCommands\":5}");
    }
    
    
    @Test
    @TestAuthor(githubId = "abass-mahdavi")
    public void noCommandMeasOneCommand() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
        String byeMessage = ((RouletteV2ClientImpl)client).getByeMessage();
        assertEquals(byeMessage,"{\"status\":\"success\",\"numberOfCommands\":1}");
    }
    
}
