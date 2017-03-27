package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Loan Lassalle
 * @author Tano Iannetta
 */
@Ignore
public class RouletteV2LassalleLoanTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = {"lassalleloan", "galahad1"})
    public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
        assertTrue(roulettePair.getClient().isConnected());
    }

    @Test
    @TestAuthor(githubId = {"lassalleloan", "galahad1"})
    public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
        int port = roulettePair.getServer().getPort();
        IRouletteV1Client client = new RouletteV1ClientImpl();
        assertFalse(client.isConnected());
        client.connect("localhost", port);
        assertTrue(client.isConnected());
    }

    @Test
    @TestAuthor(githubId = {"lassalleloan", "galahad1"})
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

    @Test
    @TestAuthor(githubId = {"lassalleloan", "galahad1"})
    public void theServerShouldReturnDataStoreCleared() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();

        List<Student> listStudents = new ArrayList<>();
        Collections.addAll(listStudents, new Student("Tano Iannetta"), new Student("Loan Lassalle"),
                new Student("Wojciech Myszkorowski"), new Student("Jérémie Zanone"));

        client.loadStudents(listStudents);
        client.clearDataStore();

        assertEquals(0, roulettePair.getClient().getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = {"lassalleloan", "galahad1"})
    public void theServerShouldSendTheListOfStudents() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();

        List<Student> listStudentExpected = new ArrayList<>();
        Collections.addAll(listStudentExpected, new Student("Tano Iannetta"),
                new Student("Loan Lassalle"), new Student("Wojciech Myszkorowski"),
                new Student("Jérémie Zanone"));

        final int sizelistStudentExpected = listStudentExpected.size();

        client.loadStudents(listStudentExpected);
        List<Student> listStudents = client.listStudents();

        for (int i = 0; i < sizelistStudentExpected; ++i) {
            assertEquals(listStudentExpected.get(i).getFullname(), listStudents.get(i).getFullname());
        }
    }
}
