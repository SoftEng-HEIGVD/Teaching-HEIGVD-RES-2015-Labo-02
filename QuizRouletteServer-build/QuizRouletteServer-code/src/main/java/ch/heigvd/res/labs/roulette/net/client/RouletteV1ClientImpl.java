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
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version
 * 1).
 *
 * @author Olivier Liechti, Thibaud Duchoud, Mario Ferreira
 */
public class RouletteV1ClientImpl implements IRouletteV1Client {

    private static final Logger LOG = Logger.getLogger(RouletteV1ClientImpl.class.getName());

    protected BufferedReader bufferedReader;
    protected PrintWriter printWriter;
    private Socket socket;

    @Override
    public void connect(String server, int port) throws IOException {
        // On instancie les champs
        socket = new Socket(server, port);
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

        // On lit le message de bienvenue
        bufferedReader.readLine();
    }

    @Override
    public void disconnect() throws IOException {
        if (isConnected()) {
            printAndFlush(RouletteV1Protocol.CMD_BYE);

            // On ferme les différents flux ouverts et le socket
            printWriter.close();
            bufferedReader.close();
            socket.close();
            
            // On les met à null
            printWriter = null;
            bufferedReader = null;
            socket = null;
        } else {
            throw new IOException("Connection error : socket not connected");
        }
    }

    @Override
    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        if (isConnected()) {
            printAndFlush(RouletteV1Protocol.CMD_LOAD);

            bufferedReader.readLine();

            printAndFlush(fullname);

            printAndFlush(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);

            bufferedReader.readLine();

        } else {
            throw new IOException("Connection error : socket not connected");
        }
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        if (isConnected()) {
            printAndFlush(RouletteV1Protocol.CMD_LOAD);

            bufferedReader.readLine();

            for (Student s : students) {
                printWriter.println(s.getFullname());
            }

            printAndFlush(RouletteV1Protocol.CMD_LOAD_ENDOFDATA_MARKER);

            bufferedReader.readLine();
        } else {
            throw new IOException("Connection error : socket not connected");
        }
    }

    @Override
    public Student pickRandomStudent() throws EmptyStoreException, IOException {
        if (isConnected()) {
            printAndFlush(RouletteV1Protocol.CMD_RANDOM);

            // On convertit le string en RandomCommandResponse (déserialise)
            RandomCommandResponse randomCommandResponse = JsonObjectMapper.parseJson(bufferedReader.readLine(), RandomCommandResponse.class);

            // Si l'on a une erreur, on lève une exception
            if (randomCommandResponse.getError() != null) {
                throw new EmptyStoreException();
            }

            return new Student(randomCommandResponse.getFullname());
        } else {
            throw new IOException("Connection error : socket not connected");
        }
    }

    @Override
    public int getNumberOfStudents() throws IOException {
        if (isConnected()) {
            printAndFlush(RouletteV1Protocol.CMD_INFO);

            // On convertit le string en InfoCommandResponse (déserialise)
            InfoCommandResponse infoCommandResponse = JsonObjectMapper.parseJson(bufferedReader.readLine(), InfoCommandResponse.class);

            return infoCommandResponse.getNumberOfStudents();
        } else {
            throw new IOException("Connection error : socket not connected");
        }
    }

    @Override
    public String getProtocolVersion() throws IOException {
        if (isConnected()) {
            printAndFlush(RouletteV1Protocol.CMD_INFO);

            // On convertit le string en InfoCommandResponse (déserialise)
            InfoCommandResponse infoCommandResponse = JsonObjectMapper.parseJson(bufferedReader.readLine(), InfoCommandResponse.class);

            return infoCommandResponse.getProtocolVersion();
        } else {
            throw new IOException("Connection error : socket not connected");
        }
    }

    // Appelle la méthode println(text) et flush() du PrintWriter de la classe
    protected void printAndFlush(String text) {
        printWriter.println(text);
        printWriter.flush();
    }

}
