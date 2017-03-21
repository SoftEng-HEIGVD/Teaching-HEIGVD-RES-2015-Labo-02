package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
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
    private PrintWriter outputWriter;
    private BufferedReader inputReader;


    @Override
    public void connect(String server, int port) throws IOException {
        socket = new Socket(server, port);
        outputWriter = new PrintWriter(socket.getOutputStream());

        inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //Ignore la première ligne d'info reçue
        inputReader.readLine();
        System.out.println("-----------READED");
    }

    @Override
    public void disconnect() throws IOException {
        outputWriter.println(RouletteV1Protocol.CMD_BYE);
        
        outputWriter.close();
        inputReader.close();
        socket.close();

        socket = null;
        outputWriter = null;
        inputReader = null;
    }

    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        outputWriter.println(RouletteV1Protocol.CMD_LOAD);
        
        outputWriter.println(fullname);
        outputWriter.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        outputWriter.flush();
        
        //Ignore les infos du serveurs
        inputReader.readLine(); //Info
        inputReader.readLine(); //Load done
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        outputWriter.println(RouletteV1Protocol.CMD_LOAD);
        for (Student student : students) {
            outputWriter.println(student.getFullname());
        }
        outputWriter.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        outputWriter.flush();
        
        //Ignore les infos du serveurs
        inputReader.readLine(); //Info
        inputReader.readLine(); //Load done        
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        outputWriter.println(RouletteV1Protocol.CMD_RANDOM);
        outputWriter.flush();

        RandomCommandResponse response
                = JsonObjectMapper.parseJson(inputReader.readLine(), RandomCommandResponse.class);

        System.out.println("BLA ------------- " + response.getError());

        if (!response.getError().isEmpty()) {
            throw new EmptyStoreException();
        }

        return new Student(response.getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        outputWriter.println(RouletteV1Protocol.CMD_INFO);        
        outputWriter.flush();
        
        InfoCommandResponse response
                = JsonObjectMapper.parseJson(inputReader.readLine(), InfoCommandResponse.class);

        return response.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        outputWriter.println(RouletteV1Protocol.CMD_INFO);        
        outputWriter.flush();
        
        InfoCommandResponse response
                = JsonObjectMapper.parseJson(inputReader.readLine(), InfoCommandResponse.class);

        return response.getProtocolVersion();
    }

}
