package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti, Mathias Dolt
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

   @Override
   public void clearDataStore() throws IOException {
      if (isConnected()) {
         writer.println(RouletteV2Protocol.CMD_CLEAR);
         writer.flush();

         if (!reader.readLine().equals(RouletteV2Protocol.RESPONSE_CLEAR_DONE)) {
            throw new IOException("Error in protocol communication");
         }
      } else {
         throw new IOException("Not connected");
      }
   }

   @Override
   public List<Student> listStudents() throws IOException {
      if (isConnected()) {
         writer.println(RouletteV2Protocol.CMD_LIST);
         writer.flush();
         
         return JsonObjectMapper.parseJson(reader.readLine(), StudentsList.class).getStudents();
      } else {
         throw new IOException("Not connected");
      }
   }

}
