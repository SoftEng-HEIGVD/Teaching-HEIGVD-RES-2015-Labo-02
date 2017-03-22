package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Ignore;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;


/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Julien Brêchet
 * @author Adrien Marco
 */

@Ignore
public class RouletteV2JulienBrechetTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);
  
  @Test
  @TestAuthor(githubId = "JulienBrechet")
  public void CorrectNumberOfStudentsInStore(){
     assertEquals(0, roulettePair.getClient().getNumberOfStudents());
     roulettePair.getClient().loadStudent("Lucas");
     assertEquals(1, roulettePair.getClient().getNumberOfStudents());
     roulettePair.getClient().loadStudent("Ali");
     assertEquals(2, roulettePair.getClient().getNumberOfStudents());
     ((RouletteV2ClientImpl)roulettePair.getClient()).clearDataStore();
     assertEquals(0, roulettePair.getClient().getNumberOfStudents());
  }
  
  
  @Test
  @TestAuthor(githubId = "JulienBrechet")
  public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }
  
  @Test
  @TestAuthor(githubId = "JulienBrechet")
  public void ClearStudentsStoreMeansThereIsNoMoreStudents() throws IOException{
     IRouletteV2Client client = new RouletteV2ClientImpl();
     client.connect("localhost", roulettePair.getServer().getPort());
     client.loadStudent("adrien marco");
     client.clearDataStore();
     assertEquals(0, client.getNumberOfStudents());
     client.disconnect();
  }
  
  
  @Test
  @TestAuthor(githubId = "JulienBrechet")
  public void ClientV2CanConnectToARouletteServer() throws Exception {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    assertFalse(client.isConnected());
    client.connect("localhost", port);
    assertTrue(client.isConnected());
    client.disconnect();
  }
  
  
  @Test
  @TestAuthor(githubId = "JulienBrechet")
  public void NumberOfCommandsIsCorrect()throws IOException{
  
     Socket socket = new Socket("localhost", roulettePair.getServer().getPort());
     BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
     PrintWriter pr = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
     
     String messageFromServer = br.readLine();
     pr.println("BYE");
     pr.flush();
     String responseToCommandBYE = br.readLine();
     String expected = "{\"status\":\"success\",\"numberOfCommands\":1}";
     
     assertEquals(responseToCommandBYE, expected);
     
  }

  
}
