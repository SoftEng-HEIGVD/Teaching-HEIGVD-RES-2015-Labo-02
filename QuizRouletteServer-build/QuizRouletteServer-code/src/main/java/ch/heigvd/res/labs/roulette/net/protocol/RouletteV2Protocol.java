package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class defines constants for the Roulette Protocol (version 2)
 *
 * @author Olivier Liechti
 * @author Matthieu Chatelan
 * @author Lara Chauffoureaux
 */
public class RouletteV2Protocol extends RouletteV1Protocol
{
   public final static String VERSION = "2.0";
   
   // Port 2613 according to the given data
   public final static int DEFAULT_PORT = 2613;

   public final static String CMD_CLEAR = "CLEAR";
   public final static String CMD_LIST = "LIST";

   // Constant string success added
   public final static String SUCCESS = "success";

   public static String RESPONSE_CLEAR_DONE = "DATASTORE CLEARED";

   public final static String[] SUPPORTED_COMMANDS = new String[]{CMD_HELP, CMD_RANDOM, CMD_LOAD, CMD_INFO, CMD_BYE, CMD_CLEAR, CMD_LIST};
}
