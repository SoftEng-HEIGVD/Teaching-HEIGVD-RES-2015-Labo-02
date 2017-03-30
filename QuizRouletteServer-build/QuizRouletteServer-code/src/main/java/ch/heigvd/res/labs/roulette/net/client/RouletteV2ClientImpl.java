package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV1Protocol;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

   @Override
   public void clearDataStore() throws IOException {
      if (!clientSocket.isConnected()) {
         throw new IOException("Client not connected");
      }

      out.println(RouletteV2Protocol.CMD_CLEAR);
      out.flush();

      if (!in.readLine().equals(RouletteV2Protocol.RESPONSE_CLEAR_DONE)) {
         throw new IOException("Bad server response");
      }

   }

   @Override
   public List<Student> listStudents() throws IOException {
      if (!clientSocket.isConnected()) {
         throw new IOException("Client not connected");
      }
      
      out.println(RouletteV2Protocol.CMD_LIST);
      out.flush();

      StudentsList response = JsonObjectMapper.parseJson(in.readLine(), StudentsList.class);

      return response.getStudents();

   }

}
