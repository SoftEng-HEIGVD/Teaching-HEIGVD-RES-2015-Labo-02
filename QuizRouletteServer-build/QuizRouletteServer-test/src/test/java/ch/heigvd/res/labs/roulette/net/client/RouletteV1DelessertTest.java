package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.LinkedList;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 1)
 * 
 * @author Simon
 */
public class RouletteV1BaehlerTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Rule
	public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV1Protocol.VERSION);

	@Test
	@TestAuthor(githubId = {"simon-baehler", "ArmandDelessert"})
	public void clientConection() throws IOException {
		IRouletteV1Client client = new RouletteV1ClientImpl();
		client.connect("localhost", roulettePair.getServer().getPort());
		assertTrue(client.isConnected());
	}

	@Test
	@TestAuthor(githubId = {"simon-baehler", "ArmandDelessert"})
	public void clientConectionByTelnet() throws IOException {
		IRouletteV1Client client = new RouletteV1ClientImpl();
		client.connect("localhost", roulettePair.getServer().getPort());
		assertTrue(client.isConnected());
		assertTrue("telnet".equals(client.getProtocolVersion()));
	}

	@Test
	@TestAuthor(githubId = {"simon-baehler", "ArmandDelessert"})
	public void YouAreTheOnlyOne() throws IOException, EmptyStoreException {
		IRouletteV1Client client = roulettePair.getClient();
		client.loadStudent("Simon");
		Student student = client.pickRandomStudent();
		assertTrue(student.getFullname().equals("Simon"));
	}

	@Test
	@TestAuthor(githubId = {"simon-baehler", "ArmandDelessert"})
	public void YouAreNotTheOnlyOne() throws IOException, EmptyStoreException {
		IRouletteV1Client client = roulettePair.getClient();
		client.connect("localhost", roulettePair.getServer().getPort());
		assertTrue(client.isConnected());
		client.loadStudent("Simon");
		client.loadStudent("Armand");
		Student student = client.pickRandomStudent();
		assertTrue(student.getFullname().equals("Simon") || student.getFullname().equals("Armand"));
	}

	@Test
	@TestAuthor(githubId = {"simon-baehler", "ArmandDelessert"})
	public void AddStudentList() throws IOException, EmptyStoreException {
		IRouletteV1Client client = roulettePair.getClient();
		client.connect("localhost", roulettePair.getServer().getPort());
		assertTrue(client.isConnected());
		LinkedList<Student> studentList = new LinkedList();
		studentList.add(new Student("Simon"));
		studentList.add(new Student("Armand"));
		client.loadStudents(studentList);
		Student student = client.pickRandomStudent();
		assertTrue(student.getFullname().equals("Simon") || student.getFullname().equals("Armand"));
	}

	@Test
	@TestAuthor(githubId = {"simon-baehler", "ArmandDelessert"})
	public void StudentListSameSizeAsgetNumberOfStudents() throws IOException, EmptyStoreException {
		IRouletteV1Client client = roulettePair.getClient();
		client.connect("localhost", roulettePair.getServer().getPort());
		assertTrue(client.isConnected());
		LinkedList<Student> studentList = new LinkedList();
		studentList.add(new Student("Simon"));
		studentList.add(new Student("Armand"));
		client.loadStudents(studentList);
		assertTrue(studentList.size() == client.getNumberOfStudents());
	}

}
