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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version
 * 1).
 *
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    protected static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    private Socket socket = new Socket();

    protected BufferedReader reader;
    protected PrintWriter writer;

    @Override
    public void connect(String server, int port) throws IOException {
        if (isConnected()) {
            throw new IOException();
        }

        socket.connect(new InetSocketAddress(server, port));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream());
    }

    @Override
    public void disconnect() throws IOException {
        if (!isConnected()) {
            throw new IOException();
        }

        writer.println(RouletteV1Protocol.CMD_BYE);
        writer.flush();
        handleByeResponse();

        reader.close();
        writer.close();
        socket.close();
    }

    protected void handleByeResponse() throws IOException{
    }

    @Override
    public boolean isConnected() {
        return socket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        List<Student> list = new ArrayList<>();
        list.add(new Student(fullname));
        loadStudents(list);
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        if (isConnected()) {
            throw new IOException();
        }

        writer.println(RouletteV1Protocol.CMD_LOAD);
        writer.flush();
        if (!reader.readLine().equalsIgnoreCase(RouletteV1Protocol.RESPONSE_LOAD_START)) {
            throw new IOException();
        }

        for (Student student : students) {
            writer.println(student.getFullname());
        }

        writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        writer.flush();
        handleLoadResponse();
    }

    protected void handleLoadResponse() throws IOException {
        if (!reader.readLine().equalsIgnoreCase(RouletteV1Protocol.RESPONSE_LOAD_DONE)) {
            throw new IOException();
        }
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        if (!isConnected()) {
            throw new IOException();
        }

        writer.println(RouletteV1Protocol.CMD_RANDOM);
        writer.flush();

        RandomCommandResponse response = JsonObjectMapper.parseJson(reader.readLine(), RandomCommandResponse.class);
        if (response.getError() != null) {
            throw new EmptyStoreException();
        }

        return new Student(response.getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        return getInfo().getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        return getInfo().getProtocolVersion();
    }

    private InfoCommandResponse getInfo() throws IOException {
        if (!isConnected()) {
            throw new IOException();
        }

        writer.println(RouletteV1Protocol.CMD_RANDOM);
        writer.flush();

        return JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);
    }
}
