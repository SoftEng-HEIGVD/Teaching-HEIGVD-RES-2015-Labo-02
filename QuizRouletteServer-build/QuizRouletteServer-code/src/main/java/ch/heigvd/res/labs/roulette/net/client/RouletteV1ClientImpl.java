package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
 * @author Christopher Meier
 * @author Daniel Palumbo
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    private Socket clientSocket;
    BufferedWriter os;
    BufferedReader is;

    /**
     * Connect the client to the server
     * @param server the IP address or DNS name of the servr
     * @param port the TCP port on which the server is listening
     * @throws IOException
     */
    @Override
    public void connect(String server, int port) throws IOException {
        clientSocket = new Socket(server, port);
        os = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        // Welcome message
        LOG.log(Level.INFO, is.readLine());
    }

    /**
     * Disconnect client from the server and close Buffers
     * @throws IOException
     */
    @Override
    public void disconnect() throws IOException {
        os.close();
        is.close();
        clientSocket.close();
    }

    /**
     *
     * @return a boolean to know if the client is connected to the server
     */
    @Override
    public boolean isConnected() {
        if(clientSocket == null) {
            return false;
        } else {
            return clientSocket.isConnected();
        }
    }

    private void send(String rp) throws IOException {
        // Send string argument to the server
        try {
            os.write(rp);
            os.newLine();
            os.flush();
        } catch (IOException e){
            System.out.println(e);
        }
    }

    /**
     * Send one student to the server
     * @param fullname the student's full name
     * @throws IOException
     */
    @Override
    public void loadStudent(String fullname) throws IOException {
        // Send Load command
        send(RouletteV1Protocol.CMD_LOAD);

        is.readLine();

        // Send student name to the server
        send(fullname);

        // Send command that stop the load of student
        send(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);

        is.readLine();
    }

    /**
     * Send more than one student to the server
     * @param students
     * @throws IOException
     */
    @Override
    public void loadStudents(List<Student> students) throws IOException {

        send(RouletteV1Protocol.CMD_LOAD);

        is.readLine();

        for(Student s : students) {
            send(s.getFullname());
        }

        send(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);

        is.readLine();
    }

    /**
     * Get a random student name from the server
     * @return student name
     * @throws EmptyStoreException
     * @throws IOException
     */
    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {

        send(RouletteV1Protocol.CMD_RANDOM);

        String response = is.readLine();

        RandomCommandResponse r = JsonObjectMapper.parseJson(response, RandomCommandResponse.class);

        if(!r.getError().isEmpty()) {
            throw new EmptyStoreException();
        }

        return new Student(r.getFullname());
    }

    /**
     * @return the number of student
     * @throws IOException
     */
    @Override
    public int getNumberOfStudents() throws IOException {
        send(RouletteV1Protocol.CMD_INFO);

        InfoCommandResponse info = JsonObjectMapper.parseJson(is.readLine(), InfoCommandResponse.class);

        return info.getNumberOfStudents();
    }

    /**
     * @return the current version of the protocol and the number of student
     * @throws IOException
     */
    @Override
    public String getProtocolVersion() throws IOException {
        send(RouletteV1Protocol.CMD_INFO);

        InfoCommandResponse info = JsonObjectMapper.parseJson(is.readLine(), InfoCommandResponse.class);

        return info.getProtocolVersion();
    }
}
