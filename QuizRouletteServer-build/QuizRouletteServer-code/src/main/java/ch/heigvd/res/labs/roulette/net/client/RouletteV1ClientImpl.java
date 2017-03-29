package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.server.ClientWorker;
import ch.heigvd.res.labs.roulette.net.server.RouletteServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 1).
 *
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    private Socket clientSocket = null;
    private BufferedReader is = null;
    private PrintWriter os = null;

    public Socket getClientSocket() {
        return clientSocket;
    }
    
    public Socket getSocket() {
        return clientSocket;
    }
    
    /**
     * Return the IS
     * @return 
     * @throws IOException 
     */
    public BufferedReader getBufferedReader() throws IOException {
        return is;
    }
    
    /**
     * Return the OS
     * @return 
     */
    public PrintWriter getPrintWriter() {
        return os;
    }
    
    @Override
    public void connect(String server, int port) throws IOException {
        System.out.println("ch.heigvd.res.labs.roulette.net.client.RouletteV1ClientImpl.connect()");
        try {
            clientSocket = new Socket(server, port);
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (IOException ex) {
            Logger.getLogger(RouletteV1ClientImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Skip the welcome message
        is.readLine();
    }

    @Override
    public void disconnect() throws IOException {
        System.out.println("ch.heigvd.res.labs.roulette.net.client.RouletteV1ClientImpl.disconnect()");
        if (clientSocket.isConnected()) {
            os.println(RouletteV1Protocol.CMD_BYE);
            os.flush();

            clientSocket.close();
            is.close();
            os.close();
        } else {
            System.err.println("Can't disconnected (already disconnected)");
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
        System.out.println("ch.heigvd.res.labs.roulette.net.client.RouletteV1ClientImpl.loadStudent("+fullname+")");
        
        System.out.println(" > " + RouletteV1Protocol.CMD_LOAD);
        os.println(RouletteV1Protocol.CMD_LOAD);
        os.flush();
        System.out.println(" < " + is.readLine());
        
        System.out.println(" > " + fullname);
        os.println(fullname);
        
        System.out.println(" > " + RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        os.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        os.flush();
        System.out.println(" < " + is.readLine()); // NULL
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        os.println(RouletteV1Protocol.CMD_LOAD);
        os.flush();
        is.readLine();
        
        for (Student s : students) {
            os.println(s.getFullname());
        }
        
        os.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        os.flush();
        is.readLine();
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        os.println(RouletteV1Protocol.CMD_RANDOM);
        os.flush();
        
        RandomCommandResponse res = JsonObjectMapper.parseJson(is.readLine(), RandomCommandResponse.class);

        if (!res.getError().isEmpty()) {
            throw new EmptyStoreException();
        }
        
        return Student.fromJson(res.getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        os.println(RouletteV1Protocol.CMD_INFO);
        os.flush();

        InfoCommandResponse res = JsonObjectMapper.parseJson(is.readLine(), InfoCommandResponse.class);
        return res.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        os.println(RouletteV1Protocol.CMD_INFO);
        os.flush();

        InfoCommandResponse infoCommandResponse = JsonObjectMapper.parseJson(is.readLine(), InfoCommandResponse.class);
        return infoCommandResponse.getProtocolVersion();
    }
}
