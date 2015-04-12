package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * This class implements the client side of the protocol specification (version
 * 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    @Override
    public void clearDataStore() throws IOException {
        writer.println(RouletteV2Protocol.CMD_CLEAR);
        writer.flush();

        if (!readMessage().equalsIgnoreCase(RouletteV2Protocol.RESPONSE_CLEAR_DONE)) {
            throw new IOException();
        }
    }

    @Override
    public List<Student> listStudents() throws IOException {
        writer.println(RouletteV2Protocol.CMD_LIST);
        writer.flush();
        StudentsList list = JsonObjectMapper.parseJson(readMessage(), StudentsList.class);
        return list.getStudents();
    }

    @Override
    protected void handleByeResponse() throws IOException {
        String response = readMessage();
        if (!response.contains("success")) {
            throw new IOException();
        }
        LOG.log(Level.INFO, "Loaded {0} commands", response.substring(response.lastIndexOf(':'), response.length() - 1));
    }

    @Override
    protected void handleLoadResponse() throws IOException {
        String response = readMessage();
        if (!response.contains("success")) {
            throw new IOException();
        }
        LOG.log(Level.INFO, "Executed {0} students", response.substring(response.lastIndexOf(':'), response.length() - 1));
    }
}
