package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Olivier Liechti
 */

public class RouletteV2QuentingigonTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Rule
  public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

  @Test
  @TestAuthor(githubId = {"quentingigon", "MathiasGilson"})
  public void theServerShouldHaveZeroStudentsAfterAClear() throws IOException {
    IRouletteV2Client client = new RouletteV2ClientImpl();
    int port = roulettePair.getServer().getPort();

    client.connect("localhost", port);

    client.loadStudent("sacha");
    client.loadStudent("olivier");
    client.loadStudent("fabienne");
    client.clearDataStore();

    assertEquals(0, client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = {"quentingigon", "MathiasGilson"})
  public void theServerShouldListAllStudentsLoaded() throws IOException, EmptyStoreException {
    IRouletteV2Client client = new RouletteV2ClientImpl();
    int port = roulettePair.getServer().getPort();

    client.connect("localhost", port);

    client.loadStudent("sacha");
    client.loadStudent("olivier");
    client.loadStudent("fabienne");

    assertEquals(client.listStudents().size(), client.getNumberOfStudents());
  }

  @Test
  @TestAuthor(githubId = {"quentingigon", "MathiasGilson"})
  public void theServerShouldContainAllStudentsLoaded() throws IOException, EmptyStoreException {
    IRouletteV2Client client = new RouletteV2ClientImpl();
    int port = roulettePair.getServer().getPort();

    client.connect("localhost", port);

    client.loadStudent("sacha");
    client.loadStudent("olivier");
    client.loadStudent("fabienne");

    List<Student> list = client.listStudents();
    ArrayList<String> stack = new ArrayList<>();
    stack.add("sacha");
    stack.add("olivier");
    stack.add("fabienne");

    for (Student student: list) {
      if(stack.contains(student.getFullname()))
        stack.remove(student.getFullname());
    }

    assertTrue(stack.isEmpty());
  }

  @Test
  @TestAuthor(githubId = {"quentingigon", "MathiasGilson"})
  public void theServerShouldGiveTheCorrectVersionNumber() throws IOException {
    assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
  }
}
