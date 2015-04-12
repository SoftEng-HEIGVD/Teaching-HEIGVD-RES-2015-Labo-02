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
    protected Socket socket;
    protected PrintWriter writer;
    protected BufferedReader reader;
    
    @Override
    public void connect(String server, int port) throws IOException {
        //Create a socket
        socket = new Socket(server, port);
        //And create the stream
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //Consume welcome message
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
        if(socket != null)
            return !socket.isClosed();

        return false;
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        if(isConnected()){
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
        }else{
            throw new IOException("The client is not connected");
        }
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        if(isConnected()){
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
        }else{
            throw new IOException("The client is not connected");
        }
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        if(isConnected()){
            writer.println(RouletteV1Protocol.CMD_RANDOM);
            writer.flush();

            RandomCommandResponse answer = JsonObjectMapper.parseJson(reader.readLine(), RandomCommandResponse.class);

            if(answer.getFullname() == null)
                throw new EmptyStoreException();

            return new Student(answer.getFullname());
        }else{
            throw new IOException("The client is not connected");
        }
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        if(isConnected()){
            writer.println(RouletteV1Protocol.CMD_INFO);
            writer.flush();
            InfoCommandResponse answer = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);
            return answer.getNumberOfStudents();
        }else{
            throw new IOException("The client is not connected");
        }
        
    }

    @Override
    public String getProtocolVersion() throws IOException {
        if(isConnected()){
            writer.println(RouletteV1Protocol.CMD_INFO);
            writer.flush();
            InfoCommandResponse answer = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);
            return answer.getProtocolVersion();
        }else{
            throw new IOException("The client is not connected");
        }
    }

}
