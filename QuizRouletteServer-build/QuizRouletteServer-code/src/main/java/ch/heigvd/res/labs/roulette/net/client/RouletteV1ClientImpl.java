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
import java.net.ProtocolException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    // 
    private Socket socket;
    private BufferedReader in = null;
    private PrintWriter out = null;

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    @Override
    public void connect(String server, int port) throws IOException {
        socket = new Socket(server, port);

        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
        
        in.readLine();
    }

    @Override
    public void disconnect() throws IOException {
        out.println(RouletteV1Protocol.CMD_BYE);
        out.flush();
        
        in.close();
        out.close();
        socket.close();
        in = null;
        out = null;
        socket = null;
    }

    @Override
    public boolean isConnected() {
        if (socket == null){
            return false;
        } else {
            return socket.isConnected() && !socket.isClosed();
        }
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        List tmp = new LinkedList<Student>();
        tmp.add(new Student(fullname));
        loadStudents(tmp);
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        out.println(RouletteV1Protocol.CMD_LOAD);
        out.flush();
        in.readLine();
        
        for (Student student : students){
            out.println(student.getFullname());
            out.flush();
        }
        out.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        out.flush();
        in.readLine();
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        out.println(RouletteV1Protocol.CMD_RANDOM);
        out.flush();
        
        RandomCommandResponse answer = JsonObjectMapper.parseJson(in.readLine(), RandomCommandResponse.class);
        
        if (answer.getFullname() == null) {
            throw new EmptyStoreException();
        } else {
            return new Student(answer.getFullname());
        }
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        out.println(RouletteV1Protocol.CMD_INFO);
        out.flush();
        
        return JsonObjectMapper.parseJson(in.readLine(), InfoCommandResponse.class).getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        out.println(RouletteV1Protocol.CMD_INFO);
        out.flush();
        
        InfoCommandResponse info = JsonObjectMapper.parseJson(in.readLine(), InfoCommandResponse.class);
        return info.getProtocolVersion();
    }
}
