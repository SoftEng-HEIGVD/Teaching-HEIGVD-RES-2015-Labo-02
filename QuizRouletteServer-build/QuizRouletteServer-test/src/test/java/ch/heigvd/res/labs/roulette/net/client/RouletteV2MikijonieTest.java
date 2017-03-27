package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Olivier Liechti, Mika Pagani
 */
public class RouletteV2MikijonieTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

  @Test
  @TestAuthor(githubId = {"wasadigi", "mikijonie"})
  public void theTestRouletteServerShouldRunDuringTests() throws IOException {
    assertTrue(roulettePair.getServer().isRunning());
  }

  @Test
  @TestAuthor(githubId = {"wasadigi", "mikijonie"})
  public void theTestRouletteClientShouldBeConnectedWhenATestStarts() throws IOException {
    assertTrue(roulettePair.getClient().isConnected());
  }

  @Test
  @TestAuthor(githubId = {"wasadigi", "mikijonie"})
  public void itShouldBePossibleForARouletteClientToConnectToARouletteServer() throws Exception {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
    assertFalse(client.isConnected());
    client.connect("localhost", port);
    assertTrue(client.isConnected());
  }

  @Test
  @TestAuthor(githubId = {"wasadigi", "mikijonie"})
  public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }

  @Test
  @TestAuthor(githubId = {"wasadigi", "mikijonie"})
  public void theServerShouldHaveZeroStudentsAtStart() throws IOException {
    int port = roulettePair.getServer().getPort();
    IRouletteV2Client client = new RouletteV2ClientImpl();
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
  @TestAuthor(githubId = {"wasadigi", "mikijonie"})
  public void theServerShouldSendAnErrorResponseWhenRandomIsCalledAndThereIsNoStudent() throws IOException, EmptyStoreException {
    IRouletteV1Client client = roulettePair.getClient();
    exception.expect(EmptyStoreException.class);
    client.pickRandomStudent();
  }

  @Test
  @TestAuthor(github = "mikijonie")
  public void theServerShouldDeleteAllStudentAfterClear() throws IOException {
    IRouletteV1Client client = roulettePair.getClient();
    client.loadStudent("sacha");
    client.loadStudent("olivier");
    client.loadStudent("fabienne");
    assertEquals(3, client.getNumberOfStudents());
    client.sendClear();
    assertEquals(0, client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(github = "mikijonie")
  public void theServerShouldBeCountingTheNumberOfCommandsOfTheSession() throws IOException {
    IRouletteV1Client client = roulettePair.getClient();
    client.loadStudent("sacha");
    client.loadStudent("olivier");
    client.loadStudent("fabienne");
    client.sendClear();

    //Pour améliorer on pourrait appeller chaqune des commandes possibles

    assertEquals(4, client.getNumberOfCommands());
  }
}

