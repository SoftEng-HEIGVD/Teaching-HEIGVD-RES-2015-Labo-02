package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

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
    public void theServerShouldManageTwoCLients() throws IOException {
        int port = roulettePair.getServer().getPort();
        IRouletteV1Client client1 = new RouletteV1ClientImpl();
        IRouletteV1Client client2 = new RouletteV1ClientImpl();
        client1.connect("localhost",port);
        client2.connect("localhost",port);
        assertTrue(client1.isConnected());
        assertTrue(client2.isConnected());
    }

}
