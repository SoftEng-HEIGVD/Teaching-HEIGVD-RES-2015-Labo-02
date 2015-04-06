package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * This class implements the client side of the protocol specification (version
 * 2).
 *
 * @author Olivier Liechti, , Michaël Berthouzoz, Thibault Schowing
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(RouletteV2ClientImpl.class.getName());

    @Override
    public void clearDataStore() throws IOException {
        writer.println(RouletteV2Protocol.CMD_CLEAR);
        writer.flush();
        readMessage();
    }

    @Override
    public List<Student> listStudents() throws IOException {
        writer.println(RouletteV2Protocol.CMD_LIST);
        writer.flush();
        StudentsList list = JsonObjectMapper.parseJson(readMessage(), StudentsList.class);
        return list.getStudents();
    }

    protected void endLoadStudent() throws IOException {
        LoadCommandResponse lcr = JsonObjectMapper.parseJson(readMessage(), LoadCommandResponse.class);
        if (lcr.getStatus().equalsIgnoreCase("success")) {
            LOG.log(Level.INFO, "Added successfully: {0} students", lcr.getNumberNewStudents());
        } else {
            LOG.log(Level.SEVERE, "Error : Students not added...");
        }
    }
}
