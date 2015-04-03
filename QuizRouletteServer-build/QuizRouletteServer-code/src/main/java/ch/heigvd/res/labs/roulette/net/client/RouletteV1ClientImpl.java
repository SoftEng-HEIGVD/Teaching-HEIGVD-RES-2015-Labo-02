package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
    private PrintWriter writer;
    private BufferedReader reader;
    
    @Override
    public void connect(String server, int port) throws IOException {
        //Create a socket
        socket = new Socket(server, port);
        //And create the stream
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //Welcome message
        reader.readLine();
    }

    @Override
    public void disconnect() throws IOException {
        writer.println("bye");
        
        socket.shutdownInput();
        socket.shutdownOutput();
        socket.close();

    }

    @Override
    public boolean isConnected() {
        return socket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        writer.println("load");
        writer.flush();
        //Read the answer
        reader.readLine();
        
        //Write the student
        writer.println(fullname);
        writer.flush();
        
        //End the writing
        writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        writer.flush();
        
        //Read the answer
        reader.readLine();
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        writer.println(RouletteV1Protocol.CMD_LOAD);
        writer.flush();
        //Read the answer
        reader.readLine();
        
        //Write all students
        for(Student stu : students){
            writer.println(stu.getFullname());
            writer.flush();
        }
        
        //End the writing
        writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        writer.flush();
        
        //Read the answer
        reader.readLine();

    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        writer.println(RouletteV1Protocol.CMD_RANDOM);
        writer.flush();
        
        Student stu = JsonObjectMapper.parseJson(reader.readLine(), Student.class);
        
        return stu;
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getProtocolVersion() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
