package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
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

    private BufferedReader reader = null;
    private PrintWriter writer = null;

    @Override
    public void connect(String server, int port) throws IOException {
        socket = new Socket(server, port);

        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream());

        LOG.info("Connected to server.");
        LOG.info("Received: " + reader.readLine());
    }

    @Override
    public void disconnect() throws IOException {
        reader.close();
        writer.close();
        socket.close();

        reader = null;
        writer = null;
        socket = null;

        LOG.info("Disconnect from server.");
    }

    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        writer.write(RouletteV1Protocol.CMD_LOAD + "\n");
        writer.flush();

        LOG.info("Received: " + reader.readLine());

        writer.write(fullname + "\n");
        writer.write(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER + "\n");
        writer.flush();

        LOG.info("Received: " + reader.readLine());
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        writer.write(RouletteV1Protocol.CMD_LOAD + "\n");
        writer.flush();

        LOG.info("Received: " + reader.readLine());

        for(Student student : students) {
            writer.write(student.getFullname());
        }
        writer.write(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER + "\n");
        writer.flush();

        LOG.info("Received: " + reader.readLine());
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        writer.write(RouletteV1Protocol.CMD_RANDOM + "\n");
        writer.flush();

        String response = reader.readLine();
        LOG.info("Received: " + response);

        RandomCommandResponse serializedResponse = JsonObjectMapper.parseJson(response, RandomCommandResponse.class);

        if(serializedResponse.getError() != null) {
            throw new EmptyStoreException();
        }

        return new Student(serializedResponse.getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        writer.write(RouletteV1Protocol.CMD_INFO + "\n");
        writer.flush();

        String response = reader.readLine();
        LOG.info("Received: " + response);

        return JsonObjectMapper.parseJson(response, InfoCommandResponse.class).getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        writer.write(RouletteV1Protocol.CMD_INFO + "\n");
        writer.flush();

        String response = reader.readLine();
        LOG.info("Received: " + response);

        return JsonObjectMapper.parseJson(response, InfoCommandResponse.class).getProtocolVersion();
    }
}
