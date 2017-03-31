package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.JsonObjectMapper;
import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.data.StudentsList;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the client side of the protocol specification (version 2).
 *
 * @author Olivier Liechti
 * @author Matthieu Chatelan
 * @author Lara Chauffoureaux
 */
public class RouletteV2ClientImpl extends RouletteV1ClientImpl implements IRouletteV2Client
{
   private static final Logger LOG = Logger.getLogger(RouletteV2ClientImpl.class.getName());

   @Override
   public void clearDataStore() throws IOException
   {
      LOG.log(Level.FINE, "Client is requesting to clear the store");

      // Ask the server to remove the actuals students from the store
      writer.println("CLEAR");
      writer.flush();

      // Trash the reply from the server
      // We choose to not use it in our client implementation
      reader.readLine();
   }

   @Override
   public List<Student> listStudents() throws IOException
   {
      LOG.log(Level.FINE, "Client is asking the list of students");

      // Ask the server to print a list of the students present in the store
      writer.println("LIST");
      writer.flush();

      // Get the response and transform it to an "StudentsList" object
      StudentsList studentsList = JsonObjectMapper.parseJson(reader.readLine(), StudentsList.class);
      
      // Return the extracted list from the "StudentList" object
      return studentsList.getStudents();
   }
}
