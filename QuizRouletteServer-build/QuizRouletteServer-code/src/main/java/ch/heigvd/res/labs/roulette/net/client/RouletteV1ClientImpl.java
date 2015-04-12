package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.InfoCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version
 * 1).
 *
 * @author Olivier Liechti
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());
    private BufferedReader reader = null;
    private PrintWriter writer = null;
    private Socket socket = null;

    @Override
    public void connect(String server, int port) throws IOException {

        // Connection avec le serveur
        socket = new Socket(server, port);

        // le reader et writer proviennent de notre socket
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream());

        // Lecture du message de bienvenue
        reader.readLine();

    }

    @Override
    public void disconnect() throws IOException {
        writer.println(RouletteV1Protocol.CMD_BYE);
        writer.flush();
        writer.close();

        System.out.println(reader.readLine());

        socket.close();
        reader.close();
    }

    @Override
    public boolean isConnected() {

        if (socket != null) {
            return socket.isConnected();
        } else {
            return false;
        }
    }

    @Override
    public void loadStudent(String fullname) throws IOException {

        writer.println(RouletteV1Protocol.CMD_LOAD);
        writer.flush();
        // On lit le message reçu après la commande
        System.out.println(reader.readLine());

        writer.println(fullname);
        writer.flush();

        writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        writer.flush();

        // On lit le message reçu après la commande
        System.out.println(reader.readLine());

    }

    /**     
     *
     * @param students
     * @throws IOException
     */
    @Override
    public void loadStudents(List<Student> students) throws IOException {

        writer.println(RouletteV1Protocol.CMD_LOAD);
        writer.flush();
        // On lit le message reçu après la commande (RESPONSE_LOAD_START)
            System.out.println(reader.readLine());
        for (Student student : students) {            
            
            writer.println(student.getFullname());        
            
        }
        writer.flush();
        writer.println(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        writer.flush();

        // On lit le message reçu après la commande (RESPONSE_LOAD_DONE)
        System.out.println(reader.readLine());
    }

    /**
     *
     * @return
     * @throws EmptyStoreException
     * @throws IOException
     */
    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        writer.println(RouletteV1Protocol.CMD_RANDOM);
        writer.flush();
        RandomCommandResponse rcRep = JsonObjectMapper.parseJson(reader.readLine(), RandomCommandResponse.class);
        if(rcRep.getError() != null){
            throw new EmptyStoreException();
        }
        return Student.fromJson(reader.readLine());
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        writer.println(RouletteV1Protocol.CMD_INFO);
        writer.flush();

        // On déserialise le message reçu dans un objet InfoCommandResponse
        InfoCommandResponse icr = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);
        return icr.getNumberOfStudents();
    }

    @Override
    public String getProtocolVersion() throws IOException {
        writer.println(RouletteV1Protocol.CMD_INFO);
        writer.flush();

        // On déserialise le message reçu dans un objet InfoCommandResponse
        InfoCommandResponse icr = JsonObjectMapper.parseJson(reader.readLine(), InfoCommandResponse.class);
        return icr.getProtocolVersion();
    }

}
