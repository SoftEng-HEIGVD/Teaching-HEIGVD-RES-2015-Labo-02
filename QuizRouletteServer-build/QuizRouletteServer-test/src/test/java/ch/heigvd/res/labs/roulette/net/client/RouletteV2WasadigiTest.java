package ch.heigvd.res.labs.roulette.net.client;

import static ch.heigvd.res.labs.roulette.data.JsonObjectMapper.toJson;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.List;
import static org.junit.Assert.assertEquals;
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
