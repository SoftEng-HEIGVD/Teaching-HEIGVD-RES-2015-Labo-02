package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ByeCommandV2Response;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandV2Response;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    /**
     * dialogs with the server to order the clearing of the students list
     * then checks server's answer
     *
     * @throws IOException
     */
    @Override
    public void clearDataStore() throws IOException {
        // asks to clear data
        toServer.println(RouletteV2Protocol.CMD_CLEAR);
        toServer.flush();

        // fetches answer
        String serverResponse = fromServer.readLine();
        if (!serverResponse.equals(RouletteV2Protocol.CMD_CLEAR)) {
            // System.out.println("Error while clearing data : unexpected server answer.");
            throw new IOException("Error while clearing data : unexpected server answer.");
        }

    }

    @Override
    public List<Student> listStudents() throws IOException {
        // asks to retrieve the student's list
        toServer.println(RouletteV2Protocol.CMD_LIST);
        toServer.flush();

        // fetches server's answer, conversion and extraction of the List
        String serverAnswer = fromServer.readLine();
        StudentsList students = JsonObjectMapper.parseJson(serverAnswer,StudentsList.class);
        return students.getStudents();



    }

    /**
     * closing of socket in V2 : this time we expect an answer
     * TODO : refactor with V1, because the processing doesn't change, only the answer.
     *
     * @throws IOException
     */
    @Override
    public void disconnect() throws IOException {
        // "asking" to disconnect
        toServer.println(RouletteV2Protocol.CMD_BYE);
        toServer.flush();

        // fetching answer
        String serverAnswer = fromServer.readLine();
        ByeCommandV2Response bcrV2 = JsonObjectMapper.parseJson(serverAnswer, ByeCommandV2Response.class);

        // checking answer
        if (!bcrV2.getStatus().equals("success")) {
            System.out.println("Error while disconnecting : unexpected server answer");
        }
        else {
            System.out.println("client V2 closed after " + bcrV2.getNumberOfCommands() + " commands.");
        }

        // closing everything
        toServer.close();
        fromServer.close();
        clientSocket.close();
    }



    /**
     * dialoging (cf protocol) with the server to input a student into the server
     * checks the answers of the server and expects the correct message to continue.
     * Otherwise it will return.
     * This method should return a boolean
     *
     * this V2 version throws an exception if the answer of the server is unsatisfactory
     *  TODO : !!!!!!!!!!!! INVERT nested call with loadStudentSSSS(list)
     *  it will be more efficient, proper usage of loadCommandReponse can be used and it wont count many single students commands
     *  TODO : refactor with V1, because the processing dosen't change, only the answer.
     *
     * @param fullname the student's full name
     * @throws IOException
     */
    @Override
    public void loadStudent(String fullname) throws IOException {
        // ask to load data
        toServer.println(RouletteV2Protocol.CMD_LOAD);
        toServer.flush();
        // wait for answer
        String serverResponse = fromServer.readLine();
        //check the answer
        if (serverResponse.equals(RouletteV2Protocol.RESPONSE_LOAD_START)) {
            // write if ok
            toServer.println(fullname);
            toServer.flush();
            toServer.println(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
            toServer.flush();
        }
        else {
            // problem
            //System.out.println("server didn't allow to load data");
            //return;
            throw new IOException("error while loading a Student : unexpected server answer.");
        }

        // wait for acknowledgement from server
        serverResponse = fromServer.readLine();
        LoadCommandV2Response lcrV2 = JsonObjectMapper.parseJson(serverResponse, LoadCommandV2Response.class);
        if (lcrV2.getStatus().equals("success")) {
            // allright
            System.out.println("load acknowledged");
            return;
        }
        else {
            // problem
            System.out.println("server didn't acknowledge the load of data");
            return;
        }
    }


}
