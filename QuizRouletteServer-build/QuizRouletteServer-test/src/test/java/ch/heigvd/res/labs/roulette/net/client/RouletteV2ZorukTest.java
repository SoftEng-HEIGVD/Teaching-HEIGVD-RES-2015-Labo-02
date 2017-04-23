package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
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

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Loïc Haas
 */
public class RouletteV2ZorukTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = "Zoruk")
    public void theClientShoudBeV2() throws IOException {
        assertNotNull((RouletteV2ClientImpl)roulettePair.getClient());
    }
    
    @Test
    @TestAuthor(githubId = "Zoruk")
    public void theClearFunctionShoudBeCalledWithNoStudents() throws IOException {
        ((RouletteV2ClientImpl)roulettePair.getClient()).clearDataStore();
    }
    
    @Test
    @TestAuthor(githubId = "Zoruk")
    public void theClearFunctionShoudRemoveAllStudents() throws IOException {
        roulettePair.getClient().loadStudent("Super student");
        ((RouletteV2ClientImpl)roulettePair.getClient()).clearDataStore();
        assertEquals(roulettePair.getClient().getNumberOfStudents(), 1);
    }
}
