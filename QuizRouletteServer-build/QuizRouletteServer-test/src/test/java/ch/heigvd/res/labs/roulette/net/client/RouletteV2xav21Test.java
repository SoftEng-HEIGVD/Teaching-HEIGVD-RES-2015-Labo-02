package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This class contains automated tests to validate the client and the server
 * implementation of the Roulette Protocol (version 2)
 *
 * @author Xavier Vaz Afonso
 * @author Gaëtan Othenin-Girard
 */
@Ignore
public class RouletteV2xav21Test {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);

    @Test
    @TestAuthor(githubId = {"xav21", "GOthGir"})
    public void theServerShouldHaveTheDefaultPort() throws IOException {
        int defaultPort = roulettePair.getServer().getPort();
        assertEquals(RouletteV2Protocol.DEFAULT_PORT, defaultPort);
    }

    @Test
    @TestAuthor(githubId = {"xav21", "GOthGir"})
    public void theServerShouldReturnCorrectProtocolVersion() throws IOException {
        String version = roulettePair.getClient().getProtocolVersion();
        assertEquals("2.0", version);
    }

    @Test
    @TestAuthor(githubId = {"xav21", "GOthGir"})
    public void theServerShouldCorrectlyClearTheData() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

        client.loadStudent("Jean");
        client.loadStudent("Stephan");
        client.loadStudent("Eric");

        client.clearDataStore();

        int numberOfStudents = client.getNumberOfStudents();
        assertEquals(0, numberOfStudents);
    }

    @Test
    @TestAuthor(githubId = {"xav21", "GOthGir"})
    public void theServerShouldFetchesTheListOfStudentsInTheStore() throws IOException {
        IRouletteV2Client client = (IRouletteV2Client) roulettePair.getClient();

        String damien = "Damien";
        String paul = "Paul";
        String yvan = "Yvan";

        client.loadStudent(damien);
        client.loadStudent(paul);
        client.loadStudent(yvan);

        List<Student> students = client.listStudents();

        assertEquals(damien, students.get(0).getFullname());
        assertEquals(paul, students.get(1).getFullname());
        assertEquals(yvan, students.get(2).getFullname());
    }

    @Test
    @TestAuthor(githubId = {"xav21", "GOthGir"})
    public void theServerShouldReturnSuccessAndCommandsNumberAfterBye() throws IOException {
        Socket client = new Socket("localhost", roulettePair.getServer().getPort());
        PrintWriter pWriter = new PrintWriter(client.getOutputStream());
        BufferedReader bReader = new BufferedReader(new InputStreamReader(client.getInputStream()));

        pWriter.write(RouletteV2Protocol.CMD_INFO + "\n");
        pWriter.flush();

        pWriter.write(RouletteV2Protocol.CMD_HELP + "\n");
        pWriter.flush();

        pWriter.write(RouletteV2Protocol.CMD_BYE + "\n");
        pWriter.flush();

        assertEquals("{\"status\":\"success\",\"numberOfCommands\":3}", bReader.readLine());
    }
}