package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.LinkedList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Olivier Liechti
 */
public class RouletteV2FabienHenrikTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Rule
	public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

	@Test
	@TestAuthor(githubId = {"BafS", "henrikakessonheig"})
	public void testConnection() throws IOException {
		assertEquals(roulettePair.getClient().isConnected(), true);
	}

	@Test
	@TestAuthor(githubId = {"BafS", "henrikakessonheig"})
	public void pickRandomShouldThrowError() throws IOException, EmptyStoreException {
		IRouletteV1Client client = roulettePair.getClient();
		exception.expect(EmptyStoreException.class);
		client.pickRandomStudent();
	}

	@Test
	@TestAuthor(githubId = {"BafS", "henrikakessonheig"})
	public void randomStudentShouldBeLouis() throws IOException, EmptyStoreException {
		IRouletteV1Client client = roulettePair.getClient();
		client.loadStudent("Louis");
		Student s = client.pickRandomStudent();
		assertEquals("Louis", s.getFullname());
	}

	@Test
	@TestAuthor(githubId = {"BafS", "henrikakessonheig"})
	public void testToLoadMultipleStudents() throws IOException {
		LinkedList<Student> s = new LinkedList<>();
		s.add(new Student("AlbertEinstein"));
		s.add(new Student("EmilCioran"));
		s.add(new Student("AlbertCaraco"));
		s.add(new Student("LouisDeBroglie"));

		roulettePair.getClient().loadStudents(s);
	}

	@Test
	@TestAuthor(githubId = {"BafS", "henrikakessonheig"})
	public void testTheNumberOfStudents() throws IOException {
		assertEquals(5, roulettePair.getClient().getNumberOfStudents());
	}

	@Test
	@TestAuthor(githubId = {"BafS", "henrikakessonheig"})
	public void testIfitCanBeDisconnected() throws IOException {
		int port = roulettePair.getServer().getPort();
		IRouletteV2Client client = new RouletteV2ClientImpl();
		client.connect("localhost", port);
		client.disconnect();
		assertFalse(client.isConnected());
	}

}
