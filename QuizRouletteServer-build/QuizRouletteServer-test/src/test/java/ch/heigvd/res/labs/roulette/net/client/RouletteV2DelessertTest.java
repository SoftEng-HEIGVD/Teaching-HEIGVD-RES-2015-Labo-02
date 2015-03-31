package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.LinkedList;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 * 
 * @author Simon
 */
public class RouletteV2BaehlerTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Rule
	public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

	@Test
	@TestAuthor(githubId = {"simon-baehler", "ArmandDelessert"})
	public void SameProtocol() throws IOException {
		assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
	}

	@Test
	@TestAuthor(githubId = {"simon-baehler", "ArmandDelessert"})
	public void clearDataStoreStudent() throws IOException {
		IRouletteV2Client client = new RouletteV2ClientImpl();
		client.connect("localhost", roulettePair.getServer().getPort());
		assertTrue(client.isConnected());
		client.loadStudent("Simon");
		client.clearDataStore();
		assertTrue(client.getNumberOfStudents() == 0);
	}

	@Test
	@TestAuthor(githubId = {"simon-baehler", "ArmandDelessert"})
	public void AddStudent() throws IOException {
		IRouletteV2Client client = new RouletteV2ClientImpl();
		client.connect("localhost", roulettePair.getServer().getPort());
		assertTrue(client.isConnected());
		client.listStudents().add(new Student("Simon"));
		assertTrue(client.listStudents().size() == 1);
	}

	@Test
	@TestAuthor(githubId = {"simon-baehler", "ArmandDelessert"})
	public void ModifyStudentsName() throws IOException {
		IRouletteV2Client client = new RouletteV2ClientImpl();
		client.connect("localhost", roulettePair.getServer().getPort());
		assertTrue(client.isConnected());
		client.listStudents().add(new Student("Simon"));
		client.listStudents().get(0).setFullname("Armand");
		assertTrue(client.listStudents().contains("Armand"));
	}

	@Test
	@TestAuthor(githubId = {"simon-baehler", "ArmandDelessert"})
	public void RemovingOnRandomStudent() throws IOException, EmptyStoreException {
		IRouletteV2Client client = new RouletteV2ClientImpl();
		client.connect("localhost", roulettePair.getServer().getPort());
		assertTrue(client.isConnected());
		client.loadStudent("Simon");
		client.loadStudent("Armand");
		client.listStudents().remove(client.pickRandomStudent());
		assertTrue(client.getNumberOfStudents() == 1);
	}

	@Test
	@TestAuthor(githubId = {"simon-baehler", "ArmandDelessert"})
	public void GiveMeAFuckingName() throws IOException, EmptyStoreException {
		IRouletteV2Client client = new RouletteV2ClientImpl();
		client.connect("localhost", roulettePair.getServer().getPort());
		assertTrue(client.isConnected());
		LinkedList<Student> studentList = new LinkedList();
		studentList.add(new Student("Simon"));
		studentList.add(new Student("Armand"));
		client.clearDataStore();
		assertTrue(client.getNumberOfStudents() == 0);

		client.listStudents().addAll(0, studentList);
		assertTrue(studentList.size() == client.listStudents().size());
	}

}
