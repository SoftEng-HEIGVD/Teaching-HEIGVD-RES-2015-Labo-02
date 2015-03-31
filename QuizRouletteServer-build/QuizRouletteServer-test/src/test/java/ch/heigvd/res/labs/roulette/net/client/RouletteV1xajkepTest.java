package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import ch.heigvd.res.labs.roulette.data.Student;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 * 
 * @author Amine Tayaa
 * @author Benoît Zuckschwerdt
 */
public class RouletteV1xajkepTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();
  
  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV1Protocol.VERSION);
  
  @Test
  @TestAuthor(githubId = {"xajkep", "msaw"})
  public void theServerShouldSupportTwoClientConnection() throws IOException {
    IRouletteV1Client client1 = new RouletteV1ClientImpl();
    IRouletteV1Client client2 = new RouletteV1ClientImpl();
    
    int port = roulettePair.getServer().getPort();
    String host = "127.0.0.1";

    client1.connect(host, port);
    client2.connect(host, port);
    
    assertTrue(client1.isConnected() && client2.isConnected());
  }
  
  @Test
  @TestAuthor(githubId = {"xajkep", "msaw"})
  public void theServerRandomCmdShouldReturnSomething() throws IOException, EmptyStoreException {
    IRouletteV1Client client = roulettePair.getClient();
    client.loadStudent("Toto Tata");
    
    assertNotNull(client.pickRandomStudent());
  }
  
  
  @Test
  @TestAuthor(githubId = {"xajkep", "msaw"})
  public void theServerShouldKeepDataWhenClientDisconnect() throws IOException {
    IRouletteV1Client client1 = new RouletteV1ClientImpl();
    IRouletteV1Client client2 = new RouletteV1ClientImpl();
    
    int port = roulettePair.getServer().getPort();
    String host = "127.0.0.1";

    client1.connect(host, port);
    client2.connect(host, port);
    
    client1.loadStudent("Toto Tata");
    client1.disconnect();
    
    assertTrue(client2.pickRandomStudent().equals(Student("Toto Tata")));
  }
  
  
  @Test
  @TestAuthor(githubId = {"xajkep", "msaw"})
  public void theServerShouldSaveGivenData() throws IOException, EmptyStoreException {
    IRouletteV1Client client = roulettePair.getClient();
    client.loadStudent("Foo Bar");
    
    assertEquals("Foo Bar", client.pickRandomStudent());
  }

  
}
