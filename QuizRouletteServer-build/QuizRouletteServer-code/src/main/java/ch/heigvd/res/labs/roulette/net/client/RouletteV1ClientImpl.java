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
 * This class implements the client side of the protocol specification (version
 * 1).
 *
 * @author Zundler Cyrill & Wertenbroek Rick
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    private Socket clientSocket = null;
    protected BufferedReader reader = null;
    protected PrintWriter writer = null;

    @Override
    public void connect(String server, int port) throws IOException {

        try {
            if (isConnected()) {
                disconnect();
            }

            clientSocket = new Socket(server, port);
            LOG.log(Level.INFO, "Client connected to" + server + " on port" + port);
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            LOG.log(Level.INFO, "Client initialized reader");
            writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
            LOG.log(Level.INFO, "Client initialized writer");

            LOG.log(Level.INFO, "Connection success HELLO CMD : {0}", reader.readLine());
        } catch (IOException ex) {
            System.out.println("IOException");
            LOG.log(Level.SEVERE, null, ex);
            throw ex; // to pass all test but it's not in Roulette Specs :/
        }
    }

    @Override
    public void disconnect() throws IOException {
        try {
            if (isConnected()) {
                writer.println(RouletteV1Protocol.CMD_BYE);
                writer.flush();
                clientSocket.close();
                writer.close();
                reader.close();
                clientSocket = null;
                writer = null;
                reader = null;
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean isConnected() {
        if (clientSocket == null) {
            return false;
        } else if (clientSocket.isConnected()) {
            return true;
        }
        return false;
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        if (isConnected()) {
            writer.println(RouletteV1Protocol.CMD_LOAD);
            writer.flush();
            LOG.log(Level.INFO, "Load Student Start Response : {0}", reader.readLine());
            writer.println(fullname);
            writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
            writer.flush();
            LOG.log(Level.INFO, "Load Student End Response : {0}", reader.readLine());
        } else {
            throw new IOException("client not connected");
        }
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        if (isConnected()) {
            writer.println(RouletteV1Protocol.CMD_LOAD);
            writer.flush();

            LOG.log(Level.INFO, "Load Students Start Response : {0}", reader.readLine());

            for (Student student : students) {
                writer.println(student.getFullname());
            }

            writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
            writer.flush();

            LOG.log(Level.INFO, "Load Students End Response : {0}", reader.readLine());
        } else {
            throw new IOException("client not connected");
        }
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        if (isConnected()) {
            writer.println(RouletteV1Protocol.CMD_RANDOM);
            writer.flush();

            String json = reader.readLine();

            RandomCommandResponse random = JsonObjectMapper.parseJson(json, RandomCommandResponse.class);

            if (random.getError() != null) {
                throw new EmptyStoreException();
            }

            return new Student(random.getFullname());
        } else {
            throw new IOException("client not connected");
        }

    }

    @Override
    public int getNumberOfStudents() throws IOException {
        if (isConnected()) {
            writer.println(RouletteV1Protocol.CMD_INFO);
            writer.flush();
            String json = reader.readLine();

            LOG.log(Level.INFO, "Response string {0}", json);
            return JsonObjectMapper.parseJson(json, InfoCommandResponse.class).getNumberOfStudents();
        } else {
            throw new IOException("client not connected");
        }
    }

    @Override
    public String getProtocolVersion() throws IOException {
        if (isConnected()) {
            writer.println(RouletteV1Protocol.CMD_INFO);
            writer.flush();
            String json = reader.readLine();

            return JsonObjectMapper.parseJson(json, InfoCommandResponse.class).getProtocolVersion();
        } else {
            throw new IOException("client not connected");
        }
    }
}
