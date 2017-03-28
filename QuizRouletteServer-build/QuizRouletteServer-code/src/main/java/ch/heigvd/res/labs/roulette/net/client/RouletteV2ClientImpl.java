package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.EmptyStoreException;
import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RandomCommandResponse;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version
 * 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    @Override
    public void clearDataStore() throws IOException {
        outputWriter.println(RouletteV2Protocol.CMD_CLEAR);
        outputWriter.flush();

        //waiting for a response
        inputReader.readLine();
    }

    @Override
    public List<Student> listStudents() throws IOException {
        //throw new UnsupportedOperationException("Not supported yet2."); //To change body of generated methods, choose Tools | Templates.
        outputWriter.println(RouletteV2Protocol.CMD_LIST);
        outputWriter.flush();

        //Wait and parse the response
        StudentsList response = JsonObjectMapper.parseJson(
                inputReader.readLine(), StudentsList.class);

        
        return response.getStudents();
    }

}
