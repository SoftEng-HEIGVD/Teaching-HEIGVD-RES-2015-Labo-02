package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import java.util.Random;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version
 * 1).
 *
 * @author silver kameni
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    //create a client socket 
   private Socket myClientSocket;
    // we need to create a boolean initialise on false to specify that we are connected
   private boolean connected = false;
    //create a buffer reader
   private BufferedReader bf;
    // create an print writer;
   private PrintWriter pw;
    //create a list of student to load all students 
   private List<Student> myStudentList = new ArrayList<>();

    @Override
    public void connect(String server, int port) throws IOException {
        myClientSocket = new Socket(server, port);
        bf = new BufferedReader(new InputStreamReader(myClientSocket.getInputStream(), "UTF-8"));
        pw = new PrintWriter(new OutputStreamWriter(myClientSocket.getOutputStream(), "UTF-8"));
        connected = true;
        bf.readLine(); // read the first message off server 
    }

    @Override
    public void disconnect() throws IOException {
        if (connected == true) {
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
        if(!connected) return;
        myStudentList.add(new Student(fullname)); // load each student to the list
        loadStudents(myStudentList); // call loadStudents method and send our list

    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
         if(!connected) return;
        if (students == null || students.isEmpty()) // return if our list is empty or not existing  
        {
            return;
        }
        pw.println(RouletteV1Protocol.CMD_LOAD); // signal begin of loading
        pw.flush();
        // loading students
        for (Student myStudent : students) {
            // print student fullname if it exist   
            if (myStudent != null && !myStudent.getFullname().isEmpty()) {
                pw.println(myStudent.getFullname());
                pw.flush();
            }
        }
        pw.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER); //signal end of loading
        pw.flush();
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        if(!connected) return null;
        pw.println(RouletteV1Protocol.CMD_RANDOM);
        pw.flush();
        RandomCommandResponse responseRandom = JsonObjectMapper.parseJson(bf.readLine(), RandomCommandResponse.class);
        if (responseRandom.getError() != null) {
            throw new EmptyStoreException();
        }
        return new Student(responseRandom.getFullname());
    }

    @Override
    public int getNumberOfStudents() throws IOException {
         
        pw.println(RouletteV1Protocol.CMD_INFO);
        pw.flush();
        return myStudentList.size(); // return size of our student list
    }

    @Override
    public String getProtocolVersion() throws IOException {
        
        pw.print(RouletteV1Protocol.CMD_INFO);
        pw.flush();
        return RouletteV1Protocol.VERSION; // return version of our protocol
    }

}
