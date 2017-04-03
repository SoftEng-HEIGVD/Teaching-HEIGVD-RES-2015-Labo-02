package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.*;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    public RouletteV2ClientImpl() {
        super();
    }

    @Override
    public void clearDataStore() throws IOException {
        sendRequestToServer(RouletteV2Protocol.CMD_CLEAR);
        //if(!getServerResponse().equals(RouletteV2Protocol.RESPONSE_CLEAR_DONE))
            //throw new IOException();
        getServerResponse();
    }

    @Override
    public List<Student> listStudents() throws IOException {
        sendRequestToServer(RouletteV2Protocol.CMD_LIST);
        // get response
        String res = getServerResponse();

        StudentListResponse parsedResponse = JsonObjectMapper.parseJson(res, StudentListResponse.class);

        return parsedResponse.getStudents();
    }

    @Override
    public void disconnect() throws IOException {
        if(!clientSocket.isConnected()) {
            LOG.info("Server not connected");
            return;
        }

        sendRequestToServer(RouletteV2Protocol.CMD_BYE);

        ByeCommandResponse byeResponse = JsonObjectMapper.parseJson(getServerResponse(),
            ByeCommandResponse.class);
        clientSocket.close();
        LOG.info("status:" + byeResponse.getStatus() + ",numberOfCommands:" + byeResponse.getNbCommands());
    }

    @Override
    public void loadStudent(String fullname) throws IOException {
        // set server in load mode
        sendRequestToServer(RouletteV2Protocol.CMD_LOAD);
        getServerResponse();

        // send to student fullname
        sendRequestToServer(fullname);

        // notify the server that transmission is done
        sendRequestToServer(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);

        // get server response
        LoadCommandResponse loadResponse = JsonObjectMapper.parseJson(getServerResponse(), LoadCommandResponse.class);

        LOG.info("status:" + loadResponse.getStatus() + ",numberOfNewStudents:" + loadResponse.getNewStudents());
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        // set server in load mode
        sendRequestToServer(RouletteV2Protocol.CMD_LOAD);
        getServerResponse();

        for (Student s: students) {
            sendRequestToServer(s.getFullname());
        }

        // notify the server that transmission is done
        sendRequestToServer(RouletteV2Protocol.CMD_LOAD_ENDOFDATA_MARKER);
        // get server response
        LoadCommandResponse loadResponse = JsonObjectMapper.parseJson(getServerResponse(), LoadCommandResponse.class);

        LOG.info("status:" + loadResponse.getStatus() + ",numberOfNewStudents:" + loadResponse.getNewStudents());

    }

}
