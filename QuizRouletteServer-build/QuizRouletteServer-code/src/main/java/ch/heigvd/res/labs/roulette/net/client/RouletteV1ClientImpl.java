package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version
 * 1).
 *
 * @author Olivier Liechti
 * @author Benallal Nadir and Verdasca Jimmy
 */
public class RouletteV1ClientImpl implements IRouletteV1Client
{

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
    private Socket clientSocket;
    private OutputStream os;
    private InputStream is;
    private BufferedReader responseBuffer;
    private PrintWriter writer;

    @Override
    public void connect(String server, int port) throws IOException
    {
        clientSocket = new Socket(server, port);
        is = clientSocket.getInputStream();
        os = clientSocket.getOutputStream();

        responseBuffer = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
        
        //We read the first answer from the server
        responseBuffer.readLine();
    }

    @Override
    public void disconnect() throws IOException
    {
        if (!clientSocket.isClosed())
        {
            writer.println(RouletteV1Protocol.CMD_BYE);
            clientSocket.close();
        }
    }

    @Override
    public boolean isConnected()
    {
        return clientSocket != null && clientSocket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException
    {
        List<Student> students = new LinkedList<>();
        students.add(new Student(fullname));
        loadStudents(students);
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException
    {
        //Writing the student in the database
        writer.println(RouletteV1Protocol.CMD_LOAD);
        writer.flush();

        //Reading instructions message
        responseBuffer.readLine();

        //Writing the data in the database
        for (Student stud : students)
        {
            writer.println(stud.getFullname());
            writer.flush();
        }

        //We have finished to send the data
        writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        writer.flush();

        //Reading DATA LOADED
        responseBuffer.readLine();
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException
    {
        writer.println(RouletteV1Protocol.CMD_RANDOM);
        writer.flush();

        //Getting the answer from the RANDOM command
        String answer = responseBuffer.readLine();
        RandomCommandResponse randomAnswer = JsonObjectMapper.parseJson(answer, RandomCommandResponse.class);
        
        String fullname = randomAnswer.getFullname();
        
        //If the database was empty, we must throw an Exception
        if (!randomAnswer.getError().equals(""))
            throw new EmptyStoreException();

        return new Student(fullname);
    }

    @Override
    public int getNumberOfStudents() throws IOException
    {
        return getInfos().getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException
    {
        return getInfos().getProtocolVersion();
    }
    
    /**
     * @brief Gets the main informations about the database
     * @return the result from the INFO command sent to the server
     * @throws IOException 
     */
    private InfoCommandResponse getInfos() throws IOException
    {
        writer.println(RouletteV1Protocol.CMD_INFO);
        writer.flush();

        String answer = responseBuffer.readLine();

        return JsonObjectMapper.parseJson(answer, InfoCommandResponse.class);
    }
}
