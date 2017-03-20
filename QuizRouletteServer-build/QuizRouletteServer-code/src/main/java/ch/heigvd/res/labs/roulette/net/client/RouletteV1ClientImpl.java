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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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

    private BufferedReader inputStream = null;
    private PrintWriter outputStream = null;

    @Override
    public void connect(String server, int port) throws IOException {
        socket = new Socket(server, port);

        inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outputStream = new PrintWriter(socket.getOutputStream());

        LOG.info("Connected to server.");
        LOG.info("Received: " + inputStream.readLine());
    }

    @Override
    public void disconnect() throws IOException {
        inputStream.close();
        outputStream.close();
        socket.close();

        inputStream = null;
        outputStream = null;
        socket = null;

        LOG.info("Disconnect from server.");
    }

    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        outputStream.write(RouletteV1Protocol.CMD_LOAD + "\n");
        outputStream.flush();

        LOG.info("Received: " + inputStream.readLine());

        outputStream.write(fullname + "\n");
        outputStream.write(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER + "\n");
        outputStream.flush();

        LOG.info("Received: " + inputStream.readLine());
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        outputStream.write(RouletteV1Protocol.CMD_LOAD + "\n");
        outputStream.flush();

        LOG.info("Received: " + inputStream.readLine());

        for(Student student : students) {
            outputStream.write(student.getFullname());
        }
        outputStream.write(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER + "\n");
        outputStream.flush();

        LOG.info("Received: " + inputStream.readLine());
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        if(getNumberOfStudents() == 0) {
            throw new EmptyStoreException();
        }

        outputStream.write(RouletteV1Protocol.CMD_RANDOM);

        String response = inputStream.readLine();
        LOG.info("Received: " + response);

        return new Student(JsonObjectMapper.parseJson(response, RandomCommandResponse.class).getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        outputStream.write(RouletteV1Protocol.CMD_INFO + "\n");
        outputStream.flush();

        String response = inputStream.readLine();
        LOG.info("Received: " + response);

        return JsonObjectMapper.parseJson(response, InfoCommandResponse.class).getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        outputStream.write(RouletteV1Protocol.CMD_INFO + "\n");
        outputStream.flush();

        String response = inputStream.readLine();
        LOG.info("Received: " + response);

        return JsonObjectMapper.parseJson(response, InfoCommandResponse.class).getProtocolVersion();
    }
}
