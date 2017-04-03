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
 * @author silver kameni & nguefack zacharie
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    //create a client socket 
    Socket myClientSocket = null;
    // we need to create a boolean initialise on false to specify that we are connected
    boolean connected = false;
    //create a buffer reader
    BufferedReader bf = null;
    // create an print writer;
    PrintWriter pw = null;
   
    @Override
    public void connect(String server, int port) throws IOException {
        myClientSocket = new Socket(server, port);
        bf = new BufferedReader(new InputStreamReader(myClientSocket.getInputStream(), "UTF-8"));
        pw = new PrintWriter(new OutputStreamWriter(myClientSocket.getOutputStream(), "UTF-8"));
        if((bf.readLine())!= null){ //read the first message og server
        connected = true;
        }
    }

    @Override
    public void disconnect() throws IOException {
        if (connected == true) {
            pw.print(RouletteV1Protocol.CMD_BYE + "\n"); //signal end of connection
            pw.flush();
            myClientSocket.close(); // close client socket
            connected = false;      // put the state of connection on false
            bf.close();
            pw.close();
        }
    }

    @Override
    public boolean isConnected() {
        return connected; // return the boolean to define the state of connection 
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        if (!connected) {
            return;
        }
        pw.print(RouletteV1Protocol.CMD_LOAD + "\n");
        pw.flush();
        bf.readLine();       
        pw.print(fullname + "\n");
        pw.flush();
        pw.print(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER + "\n");
        pw.flush();
        bf.readLine();
       
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        if (!connected) {
            return;
        }

        pw.print(RouletteV1Protocol.CMD_LOAD + "\n"); // signal begin of loading
        pw.flush();
        if (!bf.readLine().equalsIgnoreCase(RouletteV1Protocol.RESPONSE_LOAD_START)) {
            LOG.log(Level.SEVERE, "not reply from server", RouletteV1Protocol.RESPONSE_LOAD_START);
            return;
        }
        // loading students
        for (Student myStudent : students) {
            pw.println(myStudent.getFullname());
            pw.flush();
        }
        pw.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER); //signal end of loading
        pw.flush();
        if (!bf.readLine().equalsIgnoreCase(RouletteV1Protocol.RESPONSE_LOAD_DONE)) {
            LOG.log(Level.SEVERE, "not reply from server", RouletteV1Protocol.RESPONSE_LOAD_DONE);
        }
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        if (!connected) {
            return null;
        }
        pw.print(RouletteV1Protocol.CMD_RANDOM + "\n");
        pw.flush();
        RandomCommandResponse responseRandom = JsonObjectMapper.parseJson(bf.readLine(), RandomCommandResponse.class);
        //throw an exception if error
        if (responseRandom.getError() != null) {
            throw new EmptyStoreException();
        }
        return new Student(responseRandom.getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {

        if (!connected) {
            return 0;
        }
        pw.print(RouletteV1Protocol.CMD_INFO + "\n");
        pw.flush();
        return JsonObjectMapper.parseJson(bf.readLine(), InfoCommandResponse.class).getNumberOfStudents(); // return size of our student list

    }

    @Override
    public String getProtocolVersion() throws IOException {
        if (!connected) {
            return null;
        }
        pw.print(RouletteV1Protocol.CMD_INFO + "\n");
        pw.flush();
        return JsonObjectMapper.parseJson(bf.readLine(), InfoCommandResponse.class).getProtocolVersion(); // return version of our protocol
    }

}
