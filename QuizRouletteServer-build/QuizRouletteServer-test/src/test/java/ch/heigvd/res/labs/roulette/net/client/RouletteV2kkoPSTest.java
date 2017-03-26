package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.kkoPS.LoadCommandV2Response;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.schoolpulse.TestAuthor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.Assert.*;

/**
 * Created by camilo on 25.03.2017.
 */
public class RouletteV2kkoPSTest {
    // creates an excpected exception to test the exceptions handling
    @Rule
    public ExpectedException exception = ExpectedException.none();

    // binding the client and the server. BUT THIS IS A V1 PROTOCOL CLIENT
    @Rule
    public EphemeralClientServerPair roulettePair = new EphemeralClientServerPair(RouletteV2Protocol.VERSION);


    /**
     * tests the correct version Number
     * @throws IOException
     */
    @Test
    @TestAuthor(githubId = "kkoPS")
    public void theServerShouldReturnTheCorrectVersionNumber() throws IOException {
        assertEquals(RouletteV2Protocol.VERSION, roulettePair.getClient().getProtocolVersion());
    }

    /**
     * creates a client, loads 2 students, checks if number of students is 2, then clears and check if number of students is 0
     * @throws IOException
     */
    @Test
    @TestAuthor (githubId = "kkoPS")
    public void noMoreStudentsInServerAfterCallingMethodClearDataStore() throws IOException {
        IRouletteV2Client clientV2 = new RouletteV2ClientImpl();
        clientV2.loadStudent("jesus");
        clientV2.loadStudent("poutre");
        assertEquals(2, clientV2.getNumberOfStudents());
        clientV2.clearDataStore();
        assertEquals(0, clientV2.getNumberOfStudents());
    }

    // response command after clear is valid

    /**
     * connects and loads 2 students, then checks the answer's status and number of new students
     * @throws IOException
     */
    @Test
    @TestAuthor (githubId = "kkoPS")
    public void theServerSendsAModifiedResponseAfterLoadingStudents() throws IOException {
        // connexion via Socket
        Socket clientSocket = new Socket("localhost", roulettePair.getServer().getPort());
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter toServer = new PrintWriter(clientSocket.getOutputStream());
        // reading the first response
        fromServer.readLine();

        // loading two students
        toServer.println(RouletteV2Protocol.CMD_LOAD);
        toServer.flush();
        // getting the response message with instructions
        fromServer.readLine();
        // actual loading of students "names"
        toServer.println("jesus");
        toServer.println("poutre");
        toServer.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);

        // checking the server answer
        String serverResponse = fromServer.readLine();
        //JsonObjectMapper.parseJson();
        LoadCommandV2Response temp = JsonObjectMapper.parseJson(serverResponse, LoadCommandV2Response.class);
        // check the status and the number of students added
        assertEquals("succes", temp.getStatus());
        assertEquals(2, temp.getNumberOfNewStudents());

        // closing everything
        fromServer.close();
        toServer.close();
        clientSocket.close();


    }


}
