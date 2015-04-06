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
 * This class implements the client side of the protocol specification (version
 * 1).
 *
 * @author Olivier Liechti, Michaël Berthouzoz, Thibault Schowing
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
    protected Socket socket = null;
    protected BufferedReader reader = null;
    protected PrintWriter writer = null;

    protected String readMessage() throws IOException {
        String line = null;
        do {
            line = reader.readLine();
        } while (line == null);
        return line;
    }

    @Override
    public void connect(String server, int port) throws IOException {
        socket = new Socket(server, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream());
        System.out.println(readMessage());
    }

    @Override
    public void disconnect() throws IOException {
        if (!socket.isClosed()) {
            writer.println(RouletteV1Protocol.CMD_BYE);
            writer.flush();
            writer.close();
            reader.close();
            socket.close();
        }
    }

    @Override
    public boolean isConnected() {
        if (socket != null) {
            return socket.isConnected() && !socket.isClosed();
        }
        return false;
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        writer.println(RouletteV1Protocol.CMD_LOAD);
        writer.flush();
        System.out.println(reader.readLine());
        writer.println(fullname);
        writer.flush();
        writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        writer.flush();
        endLoadStudent();
    }

    private void endLoadStudent() throws IOException {
        System.out.println(readMessage());
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
        RandomCommandResponse rcr = JsonObjectMapper.parseJson(readMessage(), RandomCommandResponse.class);
        if (rcr.getError() != null) {
            throw new EmptyStoreException();
        }
        return new Student(rcr.getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        writer.println(RouletteV1Protocol.CMD_INFO);
        writer.flush();
        InfoCommandResponse icr = JsonObjectMapper.parseJson(readMessage(), InfoCommandResponse.class);
        return icr.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        writer.println(RouletteV1Protocol.CMD_INFO);
        writer.flush();
        InfoCommandResponse icr = JsonObjectMapper.parseJson(readMessage(), InfoCommandResponse.class);
        return icr.getProtocolVersion();
    }
}
