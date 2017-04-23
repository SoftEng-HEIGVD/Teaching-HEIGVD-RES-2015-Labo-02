package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version
 * 2).
 *
 * @author Miguel Santamaria
 * @author Bastien Rouiller
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    @Override
    public void clearDataStore() throws IOException {
        //empty the data if it is not already empty
        if (getNumberOfStudents() != 0) {
            writer.println(RouletteV2Protocol.CMD_CLEAR);
            writer.flush();
            reader.readLine();
        }
    }

    @Override
    public List<Student> listStudents() throws IOException {
        writer.println(RouletteV2Protocol.CMD_LIST);
        writer.flush();
        //parse the response into a student list
        return Arrays.asList(JsonObjectMapper.parseJson(reader.readLine(), Student[].class));
    }

}
