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
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
    private Socket socket;
    private BufferedReader is;
    private OutputStreamWriter os;

    @Override
    public void connect(String server, int port) throws IOException {
        socket = new Socket(server, port);
        is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        os = new OutputStreamWriter(socket.getOutputStream());

        // Read welcome message
        LOG.info(is.readLine());
    }

    @Override
    public void disconnect() throws IOException {
        if (isConnected()) {
            socket.close();
            is.close();
            os.close();
        }
    }

    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        if (!isConnected()) {
            throw new IOException("Not connected to the server");
        }

        os.append("LOAD" + System.lineSeparator());
        os.flush();
        LOG.info(is.readLine());
        os.append(fullname + System.lineSeparator());
        os.append("ENDOFDATA" + System.lineSeparator());
        os.flush();
        LOG.info(is.readLine());
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        os.append("LOAD" + System.lineSeparator());
        for (Student s : students) {
            os.append(s.getFullname() + System.lineSeparator());
        }
        os.append("ENDOFDATA" + System.lineSeparator());
        os.flush();
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        if (!isConnected()) {
            throw new IOException("Not connected to the server");
        }

        if (getNumberOfStudents() < 1) {
            throw new EmptyStoreException();
        }

        os.append("RANDOM" + System.lineSeparator());
        os.flush();
        String line = is.readLine();
        LOG.info(line);
        Student student = JsonObjectMapper.parseJson(line, Student.class);
        return student;
    }
    
    private InfoCommandResponse getInfos() throws IOException {
        if (!isConnected()) {
            throw new IOException("Not connected to the server");
        }

        os.append("INFO" + System.lineSeparator());
        os.flush();
        String line = is.readLine();
        LOG.info(line);
        return JsonObjectMapper.parseJson(line, InfoCommandResponse.class);
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        
        return getInfos().getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        return getInfos().getProtocolVersion();
    }

}
