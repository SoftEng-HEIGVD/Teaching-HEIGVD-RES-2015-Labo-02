package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.net.protocol.RouletteV2Protocol;
import ch.heigvd.res.labs.roulette.data.StudentsList;
import ch.heigvd.res.labs.roulette.data.Student;
import java.io.IOException;
import java.util.List;

/**
 * This class implements the client side of the protocol specification (version
 * 2).
 *
 * @author Olivier Liechti, Sébastien Henneberger
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client {

   private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(RouletteV2ClientImpl.class.getName());

   @Override
   public void clearDataStore() throws IOException {
      //To change body of generated methods, choose Tools | Templates.
      //throw new UnsupportedOperationException("Not supported yet."); 

      writer.println(RouletteV2Protocol.CMD_CLEAR);
      writer.flush();

      reader.readLine();
   }

   @Override
   public List<Student> listStudents() throws IOException {
      // To change body of generated methods, choose Tools | Templates.
      //throw new UnsupportedOperationException("Not supported yet.");

      writer.println(RouletteV2Protocol.CMD_LIST);
      writer.flush();

      StudentsList list = JsonObjectMapper.parseJson(reader.readLine(), StudentsList.class);

      return list.getStudents();
   }

}