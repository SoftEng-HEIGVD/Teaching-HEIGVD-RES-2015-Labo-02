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
 * @author Olivier Liechti
 * @author Christpher Meier
 * @author Daniel Palumbo
 */
public class RouletteV2cmeierTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

  @Test
  @TestAuthor(githubId = {"c-meier", "danpa32"})
  public void theServerShouldHaveZeroStudentsAfterClearDataStore() throws IOException {
    IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

    client.loadStudent("daniel");
    client.loadStudent("christopher");

    assertEquals(2, client.getNumberOfStudents());

    client.clearDataStore();

    assertEquals(0, client.getNumberOfStudents());
  }
  
}
