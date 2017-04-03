package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class defines constants for the Roulette Protocol (version 2)
 *
 * @author Olivier Liechti
 */
public class RouletteV2Protocol extends RouletteV1Protocol {

  public final static String VERSION = "2.0";

  public final static int DEFAULT_PORT = 2613;

  public final static String CMD_CLEAR = "CLEAR";
  public final static String CMD_LIST = "LIST";
  public final static String CMD_HELP = "HELP";
  public final static String CMD_RANDOM = "RANDOM";
  public final static String CMD_LOAD = "LOAD";
  public final static String CMD_INFO = "INFO";
  public final static String CMD_BYE = "BYE";

  public final static String CMD_LOAD_ENDOFDATA_MARKER = "ENDOFDATA";

  public final static String RESPONSE_LOAD_START = "Send your data [end with ENDOFDATA]";

  public static String RESPONSE_CLEAR_DONE = "DATASTORE CLEARED";

  public final static String[] SUPPORTED_COMMANDS = new String[]{CMD_HELP, CMD_RANDOM, CMD_LOAD, CMD_INFO, CMD_BYE, CMD_CLEAR, CMD_LIST};

}
