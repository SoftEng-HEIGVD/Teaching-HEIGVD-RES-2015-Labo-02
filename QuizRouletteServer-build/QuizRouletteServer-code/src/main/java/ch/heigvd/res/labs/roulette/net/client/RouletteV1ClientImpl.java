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
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    private Socket clientSocket;
    BufferedWriter os;
    BufferedReader is;

    @Override
    public void connect(String server, int port) throws IOException {
        clientSocket = new Socket(server, port);
        os = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        // Welcome message
        LOG.log(Level.INFO, is.readLine());
    }

    @Override
    public void disconnect() throws IOException {
        os.close();
        is.close();
        clientSocket.close();
    }

    @Override
    public boolean isConnected() {
        if(clientSocket == null) {
            return false;
        } else {
            return clientSocket.isConnected();
        }
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        os.write(RouletteV1Protocol.CMD_LOAD);
        os.newLine();
        os.flush();

        is.readLine();

        os.write(fullname);
        os.newLine();

        os.write(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        os.newLine();
        os.flush();

        is.readLine();
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        os.write(RouletteV1Protocol.CMD_LOAD);
        os.newLine();
        os.flush();

        is.readLine();

        for(Student s : students) {
            os.write(s.getFullname());
            os.newLine();
            os.flush();
        }

        os.write(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        os.newLine();
        os.flush();

        is.readLine();
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {

        os.write(RouletteV1Protocol.CMD_RANDOM);
        os.newLine();
        os.flush();

        String response = is.readLine();

        RandomCommandResponse r = JsonObjectMapper.parseJson(response, RandomCommandResponse.class);

        if(!r.getError().isEmpty()) {
            throw new EmptyStoreException();
        }

        return new Student(r.getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        os.write(RouletteV1Protocol.CMD_INFO);
        os.newLine();
        os.flush();

        InfoCommandResponse info = JsonObjectMapper.parseJson(is.readLine(), InfoCommandResponse.class);

        return info.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        os.write(RouletteV1Protocol.CMD_INFO);
        os.newLine();
        os.flush();

        InfoCommandResponse info = JsonObjectMapper.parseJson(is.readLine(), InfoCommandResponse.class);

        return info.getProtocolVersion();
    }
}
