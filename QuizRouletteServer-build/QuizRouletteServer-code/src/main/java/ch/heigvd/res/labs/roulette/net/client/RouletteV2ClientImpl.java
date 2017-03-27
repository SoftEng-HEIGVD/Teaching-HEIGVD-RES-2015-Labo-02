package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.StudentListResponse;

import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {


    @Override
    public void clearDataStore() throws IOException {
        sendRequestToServer(RouletteV2Protocol.CMD_CLEAR);
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

}
