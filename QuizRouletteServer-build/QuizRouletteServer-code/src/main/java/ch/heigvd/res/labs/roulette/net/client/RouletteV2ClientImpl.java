
package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.LoadCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {
    
    @Override
    public void clearDataStore() throws IOException {
        out.println(RouletteV2Protocol.CMD_CLEAR);
        out.flush();
        readMessage();
    }

    @Override
    public List<Student> listStudents() throws IOException {
        out.println(RouletteV2Protocol.CMD_LIST);
        out.flush();
        StudentsList list = JsonObjectMapper.parseJson(readMessage(), StudentsList.class);
        return list.getStudents();
    }

    protected void endLoadStudent() throws IOException {
        LoadCommandResponse lcr = JsonObjectMapper.parseJson(readMessage(), LoadCommandResponse.class);
        if (lcr.getStatus().equalsIgnoreCase("success")) {
            getLogger().log(Level.INFO, "Added successfully: {0} students", lcr.getNumberNewStudents());
        } else {
            getLogger().log(Level.SEVERE, "Error : Students not added...");
        }
    }
}
