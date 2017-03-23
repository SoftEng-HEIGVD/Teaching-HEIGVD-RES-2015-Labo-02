package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import java.util.ArrayList;
import java.util.List;
import ch.heigvd.res.labs.roulette.data.Student;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Valentin Schaad
 */
public class RouletteV2SchaadTest {

   @Rule
   public ExpectedException exception = ExpectedException.none();

   @Rule
   public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

   @Test
   @TestAuthor(githubId = "schaad")
   public void itShouldReturnTheCorrectVersionNumber() throws IOException {
      assertEquals(roulettePair.getClient().getProtocolVersion(), RouletteV2Protocol.VERSION);
   }

   @Test
   @TestAuthor(githubId = "schaad")
   public void clearDataStoreShouldRemoveStudent() throws IOException {
      IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

      client.loadStudent("studentTest");

      client.clearDataStore();
      assertEquals(client.getNumberOfStudents(), 0);
   }

   @Test
   @TestAuthor(githubId = "schaad")
   public void itShouldBePossibleToClearIfEmpty() throws IOException {
      IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

      client.clearDataStore();
      client.clearDataStore();

      assertEquals(client.getNumberOfStudents(), 0);
   }

   @Test
   @TestAuthor(githubId = "schaad")
   public void itShouldBePossibleToListStudent() throws IOException {
      ArrayList<Student> tabStudent = new ArrayList();
      IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

      for (int i = 0; i < 2; ++i) {
         tabStudent.add(new Student("student " + i));
      }

      client.loadStudents(tabStudent);

      // Je ne sais pas si ce teste pourra fonctionner car je ne sais pas si 
      // assertEquals prend en compte le positionnement des éléments dans la liste.
      assertEquals(client.listStudents(), tabStudent);
   }

   @Test
   @TestAuthor(githubId = "schaad")
   public void listShouldContainCorrectNumberOfStudent() throws IOException {
      IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();
      List<Student> tabStudent = client.listStudents();

      assertEquals(tabStudent.size(), client.getNumberOfStudents());
   }

   @Test
   @TestAuthor(githubId = "schaad")
   public void theServerShouldBeClearedByAnotherClient() throws IOException {
      int port = roulettePair.getServer().getPort();
      
      IRouletteV2Client client = new RouletteV2ClientImpl();
      client.connect("localhost", port);
      
      client.clearDataStore();
      
      client.disconnect();
      
      assertEquals(roulettePair.getClient().getNumberOfStudents(), 0);
   }
}
