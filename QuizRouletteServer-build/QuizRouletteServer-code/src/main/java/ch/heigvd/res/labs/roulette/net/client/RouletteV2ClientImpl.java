package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.ListCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version
 * 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());

    @Override
    public void clearDataStore() throws IOException {
        if (isConnected()) {
            writer.println(RouletteV2Protocol.CMD_CLEAR);
            writer.flush();
            LOG.log(Level.INFO, "Clear data sotre responce : {0}", reader.readLine());
        } else {
            throw new IOException("Not connected");
        }
    }

    @Override
    public List<Student> listStudents() throws IOException {
        if (isConnected()) {
            writer.println(RouletteV2Protocol.CMD_LIST);
            writer.flush();
            
            String json = reader.readLine();
            LOG.log(Level.INFO, "Clear data sotre responce : {0}", json);
            
            ListCommandResponse response = JsonObjectMapper.parseJson(json, 
                    ListCommandResponse.class);
            
            return response.getStudents();
        } else {
            throw new IOException("Not connected");
        }}

}
