package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ListResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.LinkedList;
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
        if (!isConnected()) {
            throw new IOException("Not connected to the server");
        }
        os.append(RouletteV2Protocol.CMD_CLEAR + System.lineSeparator());
        os.flush();
        LOG.info(is.readLine());
    }

    @Override
    public List<Student> listStudents() throws IOException {
        os.append(RouletteV2Protocol.CMD_LIST + System.lineSeparator());
        os.flush();
        String line = is.readLine();

        LOG.info(line);
        ListResponse listResponse = JsonObjectMapper.parseJson(line, ListResponse.class);
        return listResponse.getStudents();
    }

    @Override
    public void loadStudents(List<Student> students) throws IOException {
        super.loadStudents(students);
        LOG.info(is.readLine());
    }

}
