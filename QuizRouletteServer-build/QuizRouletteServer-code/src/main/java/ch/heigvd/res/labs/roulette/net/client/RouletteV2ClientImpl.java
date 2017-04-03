package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;

/**
 * This class implements the client side of the protocol specification (version
 * 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    /*
     * The last Bye and Load command response recieved. 
     * We choosed to not changed the signature of the v1 function, because of
     * compatibility.
     */
    private ByeCommandResponse byeCommandResponse;
    private LoadCommandResponse loadCommandResponse;

    @Override
    public void connect(String server, int port) throws IOException {
        super.connect(server, port);

        //We reset the current responses
        byeCommandResponse = null;
        loadCommandResponse = null;
    }

    @Override
    public void clearDataStore() throws IOException {
        outputWriter.println(RouletteV2Protocol.CMD_CLEAR);
        outputWriter.flush();

        //waiting for a response
        readLine();
    }

    @Override
    public List<Student> listStudents() throws IOException {
        outputWriter.println(RouletteV2Protocol.CMD_LIST);
        outputWriter.flush();

        //Wait and parse the response
        StudentsList response = JsonObjectMapper.parseJson(
                readLine(), StudentsList.class);

        return response.getStudents();
    }

    /**
     * Disconnects from the server by issuing the 'BYE' command.
     *
     * @throws IOException
     */
    @Override
    public void disconnect() throws IOException {
        if(socket == null)
            return;
        
        outputWriter.println(RouletteV2Protocol.CMD_BYE);
        outputWriter.flush();
        //Wait and parse the response
        byeCommandResponse = JsonObjectMapper.parseJson(
                readLine(), ByeCommandResponse.class);

        System.out.println(lastStringResponse);
        
        //Close everything 
        outputWriter.close();
        inputReader.close();
        socket.close();

        //Set to null everything, to keep a valid object state
        socket = null;
        outputWriter = null;
        inputReader = null;
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        super.loadStudent(fullname);

        this.loadCommandResponse = JsonObjectMapper.parseJson(
                lastStringResponse, LoadCommandResponse.class);
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        super.loadStudents(students);

        this.loadCommandResponse = JsonObjectMapper.parseJson(
                lastStringResponse, LoadCommandResponse.class);
    }

    /**
     *
     * @return "success" if there were no probleme, "error" otherwise
     */
    public String getStatus() {

        if (byeCommandResponse != null) {
            //If there is a byeCommandResponse, we return this status
            return byeCommandResponse.getStatus();
        } else if (loadCommandResponse != null) {
            //If it's not the end of the connection, we try to return the status
            //from loadCommandResponse
            return loadCommandResponse.getStatus();
        } else { //We return null by default
            return null;
        }
    }

    /**
     *
     * @return the number of students added in the last load done
     */
    public int getNumberOfStudentsAdded() {
        if (loadCommandResponse != null) {
            return loadCommandResponse.getNumberOfNewStudents();
        } else { //If there is no loadCommandResponse, we didn't add any students
            return 0;
        }
    }

    /**
     *
     * @return the number of commands done in the last session
     * @throws Exception an Exception if the session is not closed
     */
    public int getNumberOfCommands() throws Exception {
        if (byeCommandResponse != null) {
            return byeCommandResponse.getNumberOfCommands();
        } else {
            //We choosed to throw an exception if there were no bye response
            throw new Exception("The client has to be disconnect");
        }
    }

}
