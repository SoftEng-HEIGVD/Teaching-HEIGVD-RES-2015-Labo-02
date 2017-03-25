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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * This class implements the client side of the protocol specification (version
 * 1).
 *
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
    Socket clientSocket = null;
    OutputStream os = null;
    InputStream is = null;

    BufferedReader reader;
    PrintWriter writer;

    private final List<Student> students = new ArrayList<>();

    @Override
    public void connect(String server, int port) throws IOException {
        try {
            clientSocket = new Socket(server, port);
            os = clientSocket.getOutputStream();
            is = clientSocket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is));
            writer = new PrintWriter(new OutputStreamWriter(os)); 

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void disconnect() throws IOException {
        if (clientSocket != null && isConnected()) {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(RouletteV1ClientImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                os.close();
            } catch (IOException ex) {
                Logger.getLogger(RouletteV1ClientImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                clientSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(RouletteV1ClientImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public boolean isConnected() {
        if (clientSocket == null) {
            return false;
        }
        return clientSocket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        if (clientSocket != null) {
            writer.println(RouletteV1Protocol.CMD_LOAD);
            if (reader != null) {
            reader.readLine();
            }
            writer.flush();
            writer.println(fullname);
            writer.flush();
            writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
            writer.flush();
            if (reader != null) {
            reader.readLine();
            }
        }
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        if (clientSocket != null) {
            writer.println(RouletteV1Protocol.CMD_LOAD);
            if (reader != null) {
            reader.readLine();
            }
            for (Student student : students) {
                writer.flush();
                writer.println(student.getFullname());
            }
            writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
            writer.flush();
            if (reader != null) {
            reader.readLine();
            }
        }
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        
        if (getNumberOfStudents() == 0){
            EmptyStoreException e = new EmptyStoreException();
            throw e;
        }        
        String response;
        writer.println(RouletteV1Protocol.CMD_RANDOM);
        writer.flush();
        if (reader != null) {
            reader.readLine();
        }
        if (reader != null) {
            response = reader.readLine();
            Student formatteResponse = JsonObjectMapper.parseJson(response, Student.class);
            return formatteResponse;
        } 
        else {
            IOException e = new IOException();
            throw e;
        }       
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        
        String response;
        writer.println(RouletteV1Protocol.CMD_INFO);
        writer.flush();
        if (reader != null) {
            reader.readLine();
        }
        writer.println(RouletteV1Protocol.CMD_INFO);
        writer.flush();
        if (reader != null) {
            response = reader.readLine();
            InfoCommandResponse formattedResponse = JsonObjectMapper.parseJson(response, InfoCommandResponse.class);
            return formattedResponse.getNumberOfStudents();
        } else {
            IOException e = new IOException();
            throw e;
        }
    }

    @Override
    public String getProtocolVersion() throws IOException {        
        return RouletteV1Protocol.VERSION;
    }
}
