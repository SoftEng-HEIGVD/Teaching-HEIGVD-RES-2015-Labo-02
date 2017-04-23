package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version
 * 2).
 *
 * @author Olivier Liechti, Henrik Akesson, Fabien Salathe
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    @Override
    public void clearDataStore() throws IOException {
        out.println(RouletteV2Protocol.CMD_CLEAR);
        out.flush();

        if (!super.readLine().equalsIgnoreCase(RouletteV2Protocol.RESPONSE_CLEAR_DONE)) {
            throw new IOException();
        }
    }

    @Override
    public List<Student> listStudents() throws IOException {
        out.println(RouletteV2Protocol.CMD_LIST);
        out.flush();
        StudentsList sl;
		sl = JsonObjectMapper.parseJson(super.readLine(), StudentsList.class);
        return sl.getStudents();
    }

    protected void handleByeResponse() throws IOException {
        if (!super.readLine().contains("success")) {
            throw new IOException();
        }
    }

    protected void handleLoadResponse() throws IOException {
        if (!super.readLine().contains("success")) {
            throw new IOException();
        }
    }
}