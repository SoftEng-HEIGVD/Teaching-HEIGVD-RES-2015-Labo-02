//v1
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version
 * 1).
 *
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
    private Socket sock = null;
    protected PrintWriter out;
    protected BufferedReader in;

    public Logger getLogger(){
        return LOG;
    }
    @Override
    public void connect(String server, int port) throws IOException {

        // Force to connect to a new server
        if (isConnected()) {
            disconnect();
        }

        sock = new Socket(server, port);
        out = new PrintWriter(sock.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    }

    @Override
    public void disconnect() throws IOException {
        sock.shutdownInput();
        sock.shutdownOutput();
        sock.close();
    }

    @Override
    public boolean isConnected() {
        if (sock == null) {
            return false;
        } else {
            return sock.isConnected();
        }
    }

    @Override
    public void loadStudent(String fullname) throws IOException {

        if (!isConnected()) {
            return;
        }

        out.write(RouletteV1Protocol.CMD_LOAD);
        out.flush();
        out.write(fullname);
        out.flush();
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {

        if (!isConnected()) {
            return;
        }

        for (Student student : students) {
            out.write(student.getFullname());
            out.flush();
        }

        out.write(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        out.flush();
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {

        out.write(RouletteV1Protocol.CMD_RANDOM);
        out.flush();

        Student parseJson;
        parseJson = JsonObjectMapper.parseJson(in.readLine(), Student.class);
        return new Student(parseJson.getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        out.write(RouletteV1Protocol.CMD_INFO);
        out.flush();

        InfoCommandResponse parseJson;
        parseJson = JsonObjectMapper.parseJson(in.readLine(), InfoCommandResponse.class);
        return parseJson.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        out.write(RouletteV1Protocol.CMD_INFO);
        out.flush();

        InfoCommandResponse parseJson;
        parseJson = JsonObjectMapper.parseJson(in.readLine(), InfoCommandResponse.class);
        return parseJson.getProtocolVersion();
    }
        protected String readMessage() throws IOException {
        String line = null;
        do {
            line = in.readLine();
        } while (line == null);
        return line;
    }
}
