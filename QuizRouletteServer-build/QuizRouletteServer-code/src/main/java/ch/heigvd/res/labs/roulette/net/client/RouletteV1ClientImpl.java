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
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version
 * 1).
 *
 * @author Olivier Liechti, Jerome Moret
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
    private Socket connection;
    protected BufferedReader reader;
    protected PrintWriter writer;

    @Override
    public void connect(String server, int port) throws IOException {
        connection = new Socket(server, port);
        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));
        
        reader.readLine();
    }

    @Override
    public void disconnect() throws IOException {
        if (isConnected()) {
            writer.println(RouletteV1Protocol.CMD_BYE);
            writer.flush();
            writer.close();
            reader.close();
            connection.close();
            writer = null;
            reader = null;
            connection = null;
        }
    }

    @Override
    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        writer.println(RouletteV1Protocol.CMD_LOAD);
        writer.flush();
        reader.readLine();
        
        writer.println(fullname);
        writer.flush();
        
        writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        writer.flush();
        reader.readLine();
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        for (Student s : students) {
            loadStudent(s.getFullname());
        }
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        writer.println(RouletteV1Protocol.CMD_RANDOM);
        writer.flush();
        RandomCommandResponse randomResponse = JsonObjectMapper.parseJson(reader.readLine(), RandomCommandResponse.class);
        if (randomResponse.getError() != null) {
            throw new EmptyStoreException();
        }
        return new Student(randomResponse.getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        writer.println(RouletteV1Protocol.CMD_INFO);
        writer.flush();

        InfoCommandResponse infoResponse = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);

        return infoResponse.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        writer.println(RouletteV1Protocol.CMD_INFO);
        writer.flush();

        InfoCommandResponse infoResponse = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);
        return infoResponse.getProtocolVersion();
    }

}
