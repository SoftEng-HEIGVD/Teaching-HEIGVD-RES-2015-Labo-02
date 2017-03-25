package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
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
   @TestAuthor(githubId = "Naewy")
   public void TheClearCommandShouldClearTheStore() throws IOException
   {
      // Creation of a V2 client
      IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

      List<Student> students = new ArrayList<>();
      students.add(new Student("Matthieu"));
      students.add(new Student("Lara"));
      students.add(new Student("Marc"));

      // Ask the server to load 3 students in the store
      client.loadStudents(students);     
      // Thus store must at this moment contain 3 students
      assertEquals(3, client.getNumberOfStudents());

      // Ask the server to clear the store
      client.clearDataStore();    
      // Thus the store should be empty
      assertEquals(0, client.getNumberOfStudents());
   }

   @Test
   @TestAuthor(githubId = "Newtt")
   @TestAuthor(githubId = "Naewy")
   public void TheListCommandShouldReturnTheCorrectContent() throws IOException
   {
      // Creation of a V2 client
      IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

      List<Student> students = new ArrayList<>();
      students.add(new Student("Matthieu"));
      students.add(new Student("Lara"));
      students.add(new Student("Marc"));
      
      // Ask the server to load 3 students and test if everything's ok
      client.loadStudents(students);
      assertEquals(3, client.getNumberOfStudents());

      // Ask the list of students to the server
      List<Student> response;
      response = client.listStudents();

      // For each student received in response, comparaison to the loaded one
      for (int i = 0; i < students.size(); i++)
      {
         assertTrue(students.get(i).equals(response.get(i)));
      }
   }

   @Test
   @TestAuthor(githubId = "Newtt")
   @TestAuthor(githubId = "Naewy")
   public void TheInfoCommandResponseShouldReplyWithTheCorrectProtocolVersion() throws IOException
   {
      // Creation of a V2 client and a roulette server
      IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
      RouletteServer server = roulettePair.getServer();

      // Creation of a socket to use it manually
      Socket socket = new Socket("localhost", server.getPort());

      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

      // Load a student in the store
      client.loadStudent("Marcel");
      
      // Ask the server the current info
      writer.println("INFO");
      writer.flush();

      // Read its response and compare it to protocol's specified response
      String response = reader.readLine();
      String expectedResponse = "{\"protocolVersion\":\"2.0\",\"numberOfStudents\":1}";
      
      assertEquals(response, expectedResponse);
   }
}
