package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOError;
import java.io.IOException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Olivier Liechti
 * @author Yann Mahmoudi
 */
public class RouletteV2McMoudiTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV1Protocol.VERSION);//FIXME should be V2


  @Test
  @TestAuthor(githubId = "McMoudi")
  public void plop() throws IOException {

  }


}
