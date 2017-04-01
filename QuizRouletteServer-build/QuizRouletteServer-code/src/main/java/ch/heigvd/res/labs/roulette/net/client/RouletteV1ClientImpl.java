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
 * @author Lucas Elisei (faku99)
 * @author David Truan  (Daxidz)
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    // Connection socket.
    private Socket socket = null;

    // Reader and writer.
    BufferedReader reader = null;
    PrintWriter writer = null;

    @Override
    public void connect(String server, int port) throws IOException {
        socket = new Socket(server, port);

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

        LOG.info("Connected to server.");
        // Get rid of the welcome message.
        LOG.info("RECEIVED: " + reader.readLine());
    }

    @Override
    public void disconnect() {
        try {
            reader.close();
            writer.close();
            socket.close();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }

        reader = null;
        writer = null;
        socket = null;

        LOG.info("Disconnected from server.");
    }

    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        writer.println(RouletteV1Protocol.CMD_LOAD);
        writer.flush();

        LOG.info("RECEIVED: " + reader.readLine());

        writer.println(fullname);
        writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        writer.flush();

        LOG.info("RECEIVED: " + reader.readLine());
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        writer.println(RouletteV1Protocol.CMD_LOAD);
        writer.flush();

        LOG.info("RECEIVED: " + reader.readLine());

        for(Student student : students) {
            writer.println(student.getFullname());
        }
        writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        writer.flush();

        LOG.info("RECEIVED: " + reader.readLine());
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        writer.println(RouletteV1Protocol.CMD_RANDOM);
        writer.flush();

        String response = reader.readLine();
        LOG.info("RECEIVED: " + response);

        RandomCommandResponse serializedResponse = JsonObjectMapper.parseJson(response, RandomCommandResponse.class);

        if(serializedResponse.getError() != null) {
            throw new EmptyStoreException();
        }

        return new Student(serializedResponse.getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        writer.println(RouletteV1Protocol.CMD_INFO);
        writer.flush();

        String response = reader.readLine();
        LOG.info("RECEIVED: " + response);

        return JsonObjectMapper.parseJson(response, InfoCommandResponse.class).getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        writer.println(RouletteV1Protocol.CMD_INFO);
        writer.flush();

        String response = reader.readLine();
        LOG.info("RECEIVED: " + response);

        return JsonObjectMapper.parseJson(response, InfoCommandResponse.class).getProtocolVersion();
    }
}
