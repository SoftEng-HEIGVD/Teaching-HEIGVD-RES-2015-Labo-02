package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version
 * 1).
 *
 * @author Olivier Liechti
 * @author Rémi Jacquemard
 * @author Aurélie Levy
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    protected Socket socket;
    protected PrintWriter outputWriter;
    protected BufferedReader inputReader;

    //Used to keep a trace of the last line recieved from the server
    protected String lastStringResponse;

    /* 
     * Using this instead of the readLine() method of inputReader is usefull to
     * to keep a trace of the last String received.
     */
    protected String readLine() throws IOException {
        this.lastStringResponse = inputReader.readLine();
        return lastStringResponse;
    }

    /**
     * Establishes a connection with the server, given its IP address or DNS
     * name and its port.
     *
     * @param server the IP address or DNS name of the servr
     * @param port the TCP port on which the server is listening
     * @throws java.io.IOException
     */
    @Override
    public void connect(String server, int port) throws IOException {
        //The socket, writer and reader are initialized
        socket = new Socket(server, port);
        outputWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                socket.getOutputStream(), Charset.forName("UTF-8"))));
        inputReader = new BufferedReader(new InputStreamReader(
                socket.getInputStream(), Charset.forName("UTF-8")));

        //Ignore the first line received from the server
        readLine();
    }

    /**
     * Disconnects from the server by issuing the 'BYE' command.
     *
     * @throws IOException
     */
    @Override
    public void disconnect() throws IOException {
        outputWriter.println(RouletteV1Protocol.CMD_BYE);
        outputWriter.flush();

        //Close everything 
        outputWriter.close();
        inputReader.close();
        socket.close();

        //Set to null everything, to keep a valid object state
        socket = null;
        outputWriter = null;
        inputReader = null;
    }

    /**
     * Checks if the client is connected with the server
     *
     * @return true if the client is connected with the server
     */
    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    /**
     * Adds a student in the server database, by issuing the 'LOAD' command
     *
     * @param fullname the student's full name
     * @throws IOException
     */
    @Override
    public void loadStudent(String fullname) throws IOException {
        //Ask the server to load the students
        outputWriter.println(RouletteV1Protocol.CMD_LOAD);

        //The student is written to the output
        outputWriter.println(fullname);
        outputWriter.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        outputWriter.flush();

        //Ignore server's informations 
        readLine(); //Info
        readLine(); //Load done
    }

    /**
     * Adds a list of students in the server database, by issuing the 'LOAD'
     * command
     *
     * @param students
     * @throws IOException
     */
    @Override
    public void loadStudents(List<Student> students) throws IOException {
        //Ask the server to load the students
        outputWriter.println(RouletteV1Protocol.CMD_LOAD);
        for (Student student : students) { //Write each of the students
            outputWriter.println(student.getFullname());
        }
        outputWriter.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        outputWriter.flush();

        //Ignore server's informations 
        readLine(); //Info
        readLine(); //Load done        
    }

    /**
     * Asks the server to select a random student, by issuing the 'RANDOM'
     * command and converting the result from json into a Student instance
     *
     * @return an instance of Student randomly selected by the server
     * @throws IOException
     */
    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        //Ask for a random student
        outputWriter.println(RouletteV1Protocol.CMD_RANDOM);
        outputWriter.flush();

        //Wait and parse the response
        RandomCommandResponse response = JsonObjectMapper.parseJson(
                readLine(), RandomCommandResponse.class);

        if (response == null) {
            return null;
        }

        //If the error is not empty, an error has occured
        if (!response.getError().isEmpty()) {
            throw new EmptyStoreException();
        }

        return new Student(response.getFullname());
    }

    /**
     * Asks the server how many students are in the database by issuing the
     * 'INFO' command
     *
     * @return the number of students in the database
     * @throws IOException
     */
    @Override
    public int getNumberOfStudents() throws IOException {
        //Ask for informations
        outputWriter.println(RouletteV1Protocol.CMD_INFO);
        outputWriter.flush();

        //Wait and parse the response
        InfoCommandResponse response = JsonObjectMapper.parseJson(
                readLine(), InfoCommandResponse.class);

        //Return the number of students
        return response.getNumberOfStudents();
    }

    /**
     * Returns the protocol version implemented by the server
     *
     * @return the version of the Roulette Protocol
     * @throws IOException
     */
    @Override
    public String getProtocolVersion() throws IOException {
        //Ask for informations
        outputWriter.println(RouletteV1Protocol.CMD_INFO);
        outputWriter.flush();

        //Wait and parse the response
        InfoCommandResponse response
                = JsonObjectMapper.parseJson(readLine(), InfoCommandResponse.class);

        //Return the protocol version
        return response.getProtocolVersion();
    }

}
