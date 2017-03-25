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
 * This class implements the client side of the protocol specification (version
 * 1).
 *
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    private Socket clientSocket;
    private BufferedReader is = null;
    private PrintWriter os = null;

    @Override
    public void connect(String server, int port) throws IOException {

        final String s = server;
        final int p = port;

        Thread clientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSocket = new Socket(s, p);
                    is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    os = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                } catch (IOException ex) {
                    Logger.getLogger(RouletteV1ClientImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        clientThread.start();
    }

    @Override
    public void disconnect() throws IOException {
        os.write("BYE");
        os.flush();
        
        clientSocket.close();
        is.close();
        os.close();
    }

    @Override
    public boolean isConnected() {
        return clientSocket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        os.write("LOAD");
        os.write(fullname);
        os.write("ENDOFDATA");
        os.flush();

        // print data?
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        os.write("LOAD");
        for (Student s : students) {
            os.write(s.getFullname());
        }
        os.write("ENDOFDATA");
        os.flush();

        // print data?
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        os.write("RANDOM");
        os.flush();

        String line;
        String jsonResponse = "";

        while ((line = is.readLine()) != null) {
            jsonResponse += line;
        }

        if (jsonResponse.isEmpty()) {
            throw new EmptyStoreException();
        }
        
        return Student.fromJson(jsonResponse);
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        os.write("INFO");
        os.flush();

        String line;
        String jsonResponse = "";

        while ((line = is.readLine()) != null) {
            jsonResponse += line;
        }

        InfoCommandResponse infoCommandResponse = JsonObjectMapper.parseJson(jsonResponse, InfoCommandResponse.class);
        return infoCommandResponse.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        os.write("INFO");
        os.flush();

        String line;
        String jsonResponse = "";

        while ((line = is.readLine()) != null) {
            jsonResponse += line;
        }

        InfoCommandResponse infoCommandResponse = JsonObjectMapper.parseJson(jsonResponse, InfoCommandResponse.class);
        return infoCommandResponse.getProtocolVersion();
    }
}
