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

    protected Socket socket = null;
    protected PrintWriter writer;
    protected BufferedReader reader;
    
    @Override
    public void connect(String server, int port) throws IOException {
        if (isConnected())
            disconnect();
        socket = new Socket(server, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        
        LOG.log(Level.INFO, "Connected HELLO MESSAGE : {0}", reader.readLine());
    }

    @Override
    public void disconnect() throws IOException {
        if (isConnected()) {
            writer.println(RouletteV1Protocol.CMD_BYE);
            writer.flush();
            
            socket.close();

            socket = null;
            writer = null;
            reader = null;
        } else {
            throw new IOException("Not connected");
        }
    }

    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        if (isConnected()) {
            writer.println(RouletteV1Protocol.CMD_LOAD);
            writer.flush();
            
            LOG.log(Level.INFO, "Load student hello responce : {0}", reader.readLine());
            
            writer.println(fullname);
            writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
            writer.flush();

            LOG.log(Level.INFO, "Load student bye responce : {0}", reader.readLine());
        } else {
            throw new IOException("Not connected");
        }
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        if (isConnected()) {
            writer.println(RouletteV1Protocol.CMD_LOAD);
            writer.flush();
            
            LOG.log(Level.INFO, "Load students hello responce : {0}", reader.readLine());
            
            for (Student student : students)
                writer.println(student.getFullname());

            writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
            writer.flush();
            
            LOG.log(Level.INFO, "Load students bye responce : {0}", reader.readLine());
        } else {
            throw new IOException("Not connected");
        }
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        if (isConnected()) {
            writer.println(RouletteV1Protocol.CMD_RANDOM);
            writer.flush();
            String json = reader.readLine();
            
            LOG.log(Level.INFO, "Random pick responce {0}", json);
            
            RandomCommandResponse random = 
                    JsonObjectMapper.parseJson(json, RandomCommandResponse.class);
            
            if (random.getError() != null)
                throw new EmptyStoreException();
            
            return new Student(random.getFullname());
        } else {
            throw new IOException("Not connected");
        }
    }
    
    private InfoCommandResponse getInfo() throws IOException {
        if (isConnected()) {
            writer.println(RouletteV1Protocol.CMD_INFO);
            writer.flush();
            
            String json = reader.readLine();
            
            LOG.log(Level.INFO, "INFO responce {0}", json);
            
            return JsonObjectMapper.parseJson(json, InfoCommandResponse.class);
        } else {
            throw new IOException("Not connected");
        }
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        return getInfo().getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        return getInfo().getProtocolVersion();
    }

}
