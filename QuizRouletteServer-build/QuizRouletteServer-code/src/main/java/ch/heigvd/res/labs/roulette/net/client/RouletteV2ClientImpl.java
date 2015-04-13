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
 * @author Olivier Liechti, Thibaud Duchoud, Mario Ferreira
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

    @Override
    public void clearDataStore() throws IOException {
        if (isConnected()) {
            printAndFlush(RouletteV2Protocol.CMD_CLEAR);

                bufferedReader.readLine();
        } else {
            throw new IOException("Connection error : socket not connected");
        }
    }

    @Override
    public List<Student> listStudents() throws IOException {
        if (isConnected()) {
            printAndFlush(RouletteV2Protocol.CMD_LIST);

            // On convertit le string en StudentsList (déserialise)
            StudentsList studentsList = JsonObjectMapper.parseJson(bufferedReader.readLine(), StudentsList.class);

            // On retourne la List<Student>
            return studentsList.getStudents();
        } else {
            throw new IOException("Connection error : socket not connected");
        }
    }

}
