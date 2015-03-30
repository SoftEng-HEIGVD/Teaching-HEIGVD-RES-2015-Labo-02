package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 * 
 * @author Olivier Liechti
 */
public class RouletteV1ToniDiasTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();
  
  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV1Protocol.VERSION);

  @Test
  @TestAuthor(githubId = "wasadigi")
  public void theTestRouletteServerShouldRunDuringTests() throws IOException {
    assertTrue(roulettePair.getServer().isRunning());
  }

  @Test
  @TestAuthor(githubId = "wasadigi")
  public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
    assertTrue(roulettePair.getClient().isConnected());
  }

  @Test
  @TestAuthor(githubId = "wasadigi")
  public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
    int port = roulettePair.getServer().getPort();
    IRouletteV1Client client = new RouletteV1ClientImpl();
    assertFalse(client.isConnected());
    client.connect("localhost", port);
    assertTrue(client.isConnected());
  }
  
  @Test
  @TestAuthor(githubId = "wasadigi")
  public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
    assertEquals(RouletteV1Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }

  @Test
  @TestAuthor(githubId = "wasadigi")
  public void theServerShouldHaveZeroStudentsAtStart() throws IOException {
    int port = roulettePair.getServer().getPort();
    IRouletteV1Client client = new RouletteV1ClientImpl();
    client.connect("localhost", port);
    int numberOfStudents = client.getNumberOfStudents();
    assertEquals(0, numberOfStudents);
  }

  @Test
  @TestAuthor(githubId = {"wasadigi", "SoftEng-HEIGVD"})
  public void theServerShouldStillHaveZeroStudentsAtStart() throws IOException {
    assertEquals(0, roulettePair.getClient().getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = "SoftEng-HEIGVD")
  public void theServerShouldCountStudents() throws IOException {
    IRouletteV1Client client = roulettePair.getClient();
    assertEquals(0, client.getNumberOfStudents());
    client.loadStudent("sacha");
    assertEquals(1, client.getNumberOfStudents());
    client.loadStudent("olivier");
    assertEquals(2, client.getNumberOfStudents());
    client.loadStudent("fabienne");
    assertEquals(3, client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = "wasadigi")
  public void theServerShouldSendAnErrorResponseWhenRandomIsCalledAndThereIsNoStudent() throws IOException, EmptyStoreException {
    IRouletteV1Client client = roulettePair.getClient();
    exception.expect(EmptyStoreException.class);
    client.pickRandomStudent();
  }
  
  
  /*
  ##################### MODIFICATIONS 
  */
  
  @Test
  @TestAuthor(githubId = {"ToniDias", "Brybry16"})
  public void testDisconnect() throws IOException {
      IRouletteV1Client client = roulettePair.getClient();
      int port = roulettePair.getServer().getPort();
      
      client.connect("localhost", port);
      
      client.disconnect();
      
      if(client.isConnected()){
          fail("La deconnexion n'a pas fonctionné!!!!");
      }
  }
  
  //test si il n'y a pas d'exception quand le store n'est pas vide
  @Test
  @TestAuthor(githubId = {"ToniDias", "Brybry16"})
  public void ClientShouldNotThrowExceptionWhenStoreIsNotEmpty() throws IOException {
    try {
        IRouletteV1Client client = roulettePair.getClient();
        client.loadStudent("Bryan Dias");
        client.pickRandomStudent();
    } catch (EmptyStoreException ex) {
        fail("Cette exception ne devrait pas être levée!");
    }
  }
  
  
  //tester si plusieur clients peuvent se connecter au serveur en parallèle
  @Test
  @TestAuthor(githubId = {"ToniDias", "Brybry16"})
  public void ThreeClientsCanConnectToTheServer() throws Exception {
    
    int port = roulettePair.getServer().getPort();
    IRouletteV1Client c1 = new RouletteV1ClientImpl();
    IRouletteV1Client c2 = new RouletteV1ClientImpl();
    IRouletteV1Client c3 = new RouletteV1ClientImpl();
    
    c1.connect("localhost", port);
    c2.connect("localhost", port);
    c3.connect("localhost", port);
    assertTrue(c1.isConnected());
    assertTrue(c2.isConnected());
    assertTrue(c3.isConnected());
  }
  
  //test si la deconnexion d'un serveur d'un client perturbe un autre client
  @Test
  @TestAuthor(githubId = {"ToniDias", "Brybry16"})
  public void DisconnectingAClientDoesntDisconnectTheOtherClients() throws Exception {
    
    int port = roulettePair.getServer().getPort();
    IRouletteV1Client c1 = new RouletteV1ClientImpl();
    IRouletteV1Client c2 = new RouletteV1ClientImpl();
    
    c1.connect("localhost", port);
    c2.connect("localhost", port);
    
    c1.disconnect();
    
    assertTrue(c2.isConnected());
  }
  
  //tester si un client n'envoie qu'une donnée, un 2me client ne peut recevoir que cette donnée
  @Test
  @TestAuthor(githubId = {"ToniDias", "Brybry16"})
  public void SecondClientShouldOnlyRecieveTheDataSentByFirstClient() throws IOException, EmptyStoreException {
    IRouletteV1Client client1 = roulettePair.getClient();
    String studentName = "Toni Perroud";
    client1.loadStudent(studentName);

    IRouletteV1Client client2 = new RouletteV1ClientImpl();
    client2.connect("localhost", roulettePair.getServer().getPort());
    
    Student student = client2.pickRandomStudent();
    assertEquals(student.getFullname(), studentName);
  }
  
 
  //tester si on peut envoyer des caractère spéciaux pour le nom
  @Test
  @TestAuthor(githubId = {"ToniDias", "Brybry16"})
  public void SpecialCharsCanBeSend() throws IOException, EmptyStoreException{
        IRouletteV1Client client = roulettePair.getClient();
        String name = "Bryan Dias \n Toni Perroud";
        client.loadStudent(name);
        Student student = client.pickRandomStudent();
        
        assertEquals(student.getFullname(), name);
   
  }
  
  
  
  
 
}
