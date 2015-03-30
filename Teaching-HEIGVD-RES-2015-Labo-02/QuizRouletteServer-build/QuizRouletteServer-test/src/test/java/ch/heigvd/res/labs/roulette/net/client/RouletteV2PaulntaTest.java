package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Paul Ntawuruhunga and Karim Gohzlani
 */
public class RouletteV2PaulntaTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);


    @Test
    @TestAuthor(githubId = {"wasadigi", "SoftEng-HEIGVD"})
    public void theServerShouldClearDataCorrectly() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        client.loadStudent("Paul");
        client.loadStudent("Karim");
        assertEquals(2, client.getNumberOfStudents());
        client.clearDataStore();
        assertEquals(0, client.getNumberOfStudents());
    }

    @Test
    @TestAuthor(githubId = {"wasadigi", "SoftEng-HEIGVD"})
    public void test2() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client)roulettePair.getClient();
        
    }






}
