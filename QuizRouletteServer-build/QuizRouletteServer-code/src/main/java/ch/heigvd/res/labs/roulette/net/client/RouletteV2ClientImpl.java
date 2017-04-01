package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Lucas ELISEI (faku99)
 * @author David TRUAN  (Daxidz)
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());

    @Override
    public void clearDataStore() throws IOException {
        writer.println(RouletteV2Protocol.CMD_CLEAR);
        writer.flush();

        LOG.info("RECEIVED: " + reader.readLine());
    }

    @Override
    public List<Student> listStudents() throws IOException {
        writer.println(RouletteV2Protocol.CMD_LIST);
        writer.flush();

        String response = reader.readLine();
        LOG.info("RECEIVED: " + response);

        return JsonObjectMapper.parseJson(response, StudentsList.class).getStudents();
    }

}
