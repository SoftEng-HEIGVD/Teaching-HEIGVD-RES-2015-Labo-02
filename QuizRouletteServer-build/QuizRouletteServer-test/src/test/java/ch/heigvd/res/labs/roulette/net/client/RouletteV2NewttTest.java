package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.server.RouletteServer;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Matthieu Chatelan
 * @author Lara Chauffoureaux
 */
public class RouletteV2NewttTest
{
   @Rule
   public ExpectedException exception = ExpectedException.none();

   @Rule
   public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

   @Test
   @TestAuthor(githubId = "wasadigi")
   public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException
   {
      assertTrue(roulettePair.getClient().isConnected());
   }

   @Test
   @TestAuthor(githubId = "Newtt")
   public void TheClearCommandShouldClearTheStore() throws IOException
   {
      IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

      List<Student> students = new ArrayList<>();
      students.add(new Student("Matthieu"));
      students.add(new Student("Lara"));
      students.add(new Student("Marc"));

      client.loadStudents(students);
      assertEquals(3, client.getNumberOfStudents());

      client.clearDataStore();
      assertEquals(0, client.getNumberOfStudents());
   }

   @Test
   @TestAuthor(githubId = "Newtt")
   public void TheListCommandShouldReturnTheCorrectContent() throws IOException
   {
      IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

      List<Student> students = new ArrayList<>();
      students.add(new Student("Matthieu"));
      students.add(new Student("Lara"));
      students.add(new Student("Marc"));

      client.loadStudents(students);
      assertEquals(3, client.getNumberOfStudents());

      List<Student> response;
      response = client.listStudents();

      for (int i = 0; i < students.size(); i++)
      {
         assertTrue(students.get(i).equals(response.get(i)));
      }
   }

   @Test
   @TestAuthor(githubId = "Newtt")
   public void TheInfoCommandResponseShouldReplyWithTheCorrectProtocolVersion() throws IOException
   {
      IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
      RouletteServer server = roulettePair.getServer();

      Socket socket = new Socket("localhost", server.getPort());
      client.loadStudent("Marcel");

      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

      writer.println("INFO");
      writer.flush();

      String response = reader.readLine();
      String expectedResponse = "{\"protocolVersion\":\"2.0\",\"numberOfStudents\":1}";
   }
}
