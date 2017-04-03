package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 * <p>
 * What the client does… (taken from the lectures)
 * 1. Create a socket
 * 2. Make a connection request on an IP address / port
 * 3. Read and write bytes through this socket, communicating with the client
 * 4. Close the client socket
 *
 * @author Olivier Liechti
 * @author Antoine Nourazar
 * @author Camilo Pineda Serna
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    // the client socket used to communicate with the server
    Socket clientSocket = new Socket(); // to "open" the socket. will be bound in the cstor
    // the streams for reading and writing
    BufferedReader fromServer;
    PrintWriter toServer;


    /**
     * Creation of the socket
     *
     * @param server the IP address or DNS name of the servr
     * @param port   the TCP port on which the server is listening
     * @throws IOException
     */
    @Override
    public void connect(String server, int port) throws IOException {
        clientSocket = new Socket(server, port);
        fromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        toServer = new PrintWriter(clientSocket.getOutputStream());
        // System.out.println("client creates a socket for server " + server + " on port " + port);
        // lire la reponse du serveur HELLO
        System.out.println(fromServer.readLine());
//        // repondre au serveur
//        toServer.write(RouletteV1Protocol.CMD_HELP);
//        toServer.flush();
//        // lire la seconde réponse du serveur
//        System.out.println(fromServer.readLine());

    }

    /**
     * closing of socket
     *
     * @throws IOException
     */
    @Override
    public void disconnect() throws IOException {
        toServer.println(RouletteV1Protocol.CMD_BYE);
        toServer.flush();
        // pas de reponse du serveur
        //System.out.println(fromServer.readLine());

        toServer.close();
        fromServer.close();
        clientSocket.close();

        System.out.println("closing of the client socket.");
    }

    /**
     * test connexion
     *
     * @return
     */
    @Override
    public boolean isConnected() {
        return clientSocket.isConnected();
    }

    /**
     * Changes : loadStudent will call loadStudentS
     *
     * This method calls loadStudents(..) with a list having one student
     *
     *
     * @param fullname the student's full name
     * @throws IOException
     */
    @Override
    public void loadStudent(String fullname) throws IOException {
        // we are going to use the loadStudentS() method : we'll transfert a list with one student to the method.
        List<Student> oneStudentInList = new ArrayList<>();
        Student theStudent = new Student(fullname);
        oneStudentInList.add(theStudent);
        loadStudents(oneStudentInList);
    }

    /**
     * dialogs (cf protocol) with the server to input a list of students into the server
     * checks the answers of the server and expects the correct message to continue.
     * Otherwise it will return.
     *
     * @param students
     * @throws IOException
     */
    @Override
    public void loadStudents(List<Student> students) throws IOException {
        // ask to load data
        toServer.println(RouletteV1Protocol.CMD_LOAD);
        toServer.flush();
        // wait for answer
        String serverResponse = fromServer.readLine();
        //check the answer
        if (serverResponse.equals(RouletteV1Protocol.RESPONSE_LOAD_START)) {
            // write if ok
            for (Student s : students) {
                toServer.println(s.getFullname());
                toServer.flush();
            }

            toServer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
            toServer.flush();
        }
        else {
            // problem
//            System.out.println("server didn't allow to load data");
//            return;
            throw new IOException("error trying to load students : unexpected server answer.");
        }

        /// V1 and V2 differences : two methods
        // wait for acknowledgement from server
        endLoadStudents();
    }

    /**
     * V1 for the end of the loadStudents method
     * don't forget to set the protected access rights
     * @throws IOException
     */
    protected void endLoadStudents() throws IOException {
        String serverResponse = fromServer.readLine();
        System.out.println(serverResponse);
        if (serverResponse.equals(RouletteV1Protocol.RESPONSE_LOAD_DONE)) {
            // allright
            System.out.println("load acknowledged");
            return;
        }
        else {
            // problem
//            System.out.println("server didn't acknowledge the load of data");
//            return;
            throw new IOException("error at end of loadStudents V1 : unexpected server answer");
        }
    }

    /**
     * dialog with the server to fetch a student. then converts the json to Student
     *
     * @return
     * @throws EmptyStoreException
     * @throws IOException
     */
    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        // asking to pick a student at random
        toServer.println(RouletteV1Protocol.CMD_RANDOM);
        toServer.flush();
        // get the answer
        String serverResponse = fromServer.readLine();
        RandomCommandResponse temp = JsonObjectMapper.parseJson(serverResponse, RandomCommandResponse.class);
        // if the error is not empty, throw exception
        if (!temp.getError().equals("")) {
            throw new EmptyStoreException();
        }
        else {
            // return the Student
            return Student.fromJson(serverResponse);
        }
    }

    /**
     * dialoging with the server to fetch de number of students.
     * Uses the InfoCommandRespons class
     *
     * @return
     * @throws IOException
     */
    @Override
    public int getNumberOfStudents() throws IOException {
        // question the server
        toServer.println(RouletteV1Protocol.CMD_INFO);
        toServer.flush();
        // wait the answer
        String serverResponse = fromServer.readLine();
        // deserialize and fetch the info
        return (JsonObjectMapper.parseJson(serverResponse, InfoCommandResponse.class).getNumberOfStudents());
    }

    /**
     * negotiates with the server to fetch the protocol version
     * Uses the InfoCommandRespons class
     * @return
     * @throws IOException
     */
    @Override
    public String getProtocolVersion() throws IOException {
        // question the server
        toServer.println(RouletteV1Protocol.CMD_INFO);
        toServer.flush();
        // wait the answer
        String serverResponse = fromServer.readLine();
        // deserialize and fetch the info
        return (JsonObjectMapper.parseJson(serverResponse, InfoCommandResponse.class).getProtocolVersion());
    }


}
