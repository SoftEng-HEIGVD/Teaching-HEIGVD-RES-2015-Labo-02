package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.server.RouletteServer;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Mathieu Monteverde
 */
@Ignore
public class RouletteV2MathieumonteverdeTest {

   @Rule
   public ExpectedException exception = ExpectedException.none();

   @Rule
   public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

   @Test
   @TestAuthor(githubId = "mathieumonteverde")
   public void theRouletteServerShouldRunDuringTests() {
      assertTrue(roulettePair.getServer().isRunning());
   }
   
   @Test
   @TestAuthor(githubId = "mathieumonteverde")
   public void  theRouletteClientShouldBeConnectedAtTheBeginningOfTheTest() {
      assertTrue(roulettePair.getClient().isConnected());
   }
   
   @Test
   @TestAuthor(githubId = "mathieumonteverde")
   public void theServerPortShouldBe2613() {
      RouletteServer server = roulettePair.getServer();
      int port = server.getPort();
      assertEquals(2613, port);
   }
   
   @Test
   @TestAuthor(githubId = "mathieumonteverde")
   public void theServerShouldClearTheData() throws Exception {
      // Connect to the server
      Socket socket = new Socket("localhost", 2613);
      PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
      
      // Read first line
      in.readLine();
      
      // Load some students
      out.println(RouletteV2Protocol.CMD_LOAD);
      // Read response
      in.readLine();
      
      out.println("John Doe1");
      out.println("John Doe2");
      out.println("John Doe3");
      out.flush();
      
      out.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
      
      // Read Response
      in.readLine();
      
      // Aske to clear the server's data
      out.println(RouletteV2Protocol.CMD_CLEAR);
      out.flush();
      // Read response
      in.readLine();
      
      out.println(RouletteV2Protocol.CMD_INFO);
      out.flush();
      String result = in.readLine();
      // Test if the result says that there is no Student in the database
      InfoCommandResponse response = JsonObjectMapper.parseJson(result, InfoCommandResponse.class);
      assertEquals(0, response.getNumberOfStudents());
   }
   
   @Test
   @TestAuthor(githubId = "mathieumonteverde")
   public void theRouletteClientShouldClearTheData() throws Exception{
      RouletteV2ClientImpl client = (RouletteV2ClientImpl) roulettePair.getClient();
      client.loadStudent("John Doe1");
      client.loadStudent("John Doe2");
      
      client.clearDataStore();
      
      assertEquals(0, client.getNumberOfStudents());
      
   }

}
