package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by camilo on 25.03.2017.
 */
public class RouletteV2kkoPSTest {
    // creates an excpected exception to test the exceptions handling
    @Rule
    public ExpectedException exception = ExpectedException.none();

    // binding the client and the server
    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);


    /**
     * tests the correct version Number
     * @throws IOException
     */
    @Test
    @TestAuthor(githubId = "kkoPS")
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

    /**
     * creates a client, loads 2 students, checks if number of students is 2, then clears and check if number of students is 0
     * @throws IOException
     */
    @Test
    @TestAuthor (githubId = "kkoPS")
    public void noMoreStudentsInServerAfterCallingMethodClearDataStore() throws IOException {
        IRouletteV2Client clientV2 = new RouletteV2ClientImpl();
        clientV2.loadStudent("jesus");
        clientV2.loadStudent("poutre");
        assertEquals(2, clientV2.getNumberOfStudents());
        clientV2.clearDataStore();
        assertEquals(0, clientV2.getNumberOfStudents());
    }

}
