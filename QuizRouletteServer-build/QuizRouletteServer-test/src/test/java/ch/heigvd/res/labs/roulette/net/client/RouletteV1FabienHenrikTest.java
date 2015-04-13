package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.time.Clock;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 *
 * @author Fabien
 * @author Henrik
 */
public class RouletteV1FabienHenrikTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Rule
	public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV1Protocol.VERSION);

	@Test
	@TestAuthor(githubId = {"BafS", "henrikakessonheig"})
	public void testConnection() throws IOException {
		assertEquals(roulettePair.getClient().isConnected(), true);
	}

	@Test
	@TestAuthor(githubId = {"BafS", "henrikakessonheig"})
	public void testTheDefaultPort() throws Exception {
		int port = roulettePair.server.getPort();
		assertEquals(port, RouletteV1Protocol.DEFAULT_PORT);
	}

	@Test
	@TestAuthor(githubId = {"BafS", "henrikakessonheig"})
	public void loadStudents() throws IOException {
		IRouletteV1Client client = roulettePair.getClient();
		client.loadStudent("AlbertEinstein");
		client.loadStudent("EmilCioran");
		client.loadStudent("AlbertCaraco");
		client.loadStudent("LouisDeBroglie");
		assertTrue(true);
	}

	@Test
	@TestAuthor(githubId = {"BafS", "henrikakessonheig"})
	public void theServerShouldNotSendAnError() throws IOException, EmptyStoreException {
		IRouletteV1Client client = roulettePair.getClient();
		client.pickRandomStudent();
		assertTrue(true);
	}

	@Test
	@TestAuthor(githubId = {"BafS", "henrikakessonheig"})
	public void testTheNumberOfClients() throws IOException {
		int port = roulettePair.getServer().getPort();
		IRouletteV1Client client = new RouletteV1ClientImpl();
		client.connect("localhost", port);
		int numberOfStudents = client.getNumberOfStudents();
		assertEquals(4, numberOfStudents);
	}

	@Test
	@TestAuthor(githubId = {"BafS", "henrikakessonheig"})
	public void testDisconnection() throws IOException, EmptyStoreException {
		IRouletteV1Client client = roulettePair.getClient();
		client.disconnect();
		assertFalse(client.isConnected());
	}

}
