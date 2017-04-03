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

  public final static String RESPONSE_CLEAR_DONE = "DATASTORE CLEARED";

  //----------JSON RESPONSE---------------
  //The success reponse for the json response
  public final static String RESPONSE_SUCCESS_LOWERCASE = "success";

  //not specified by protocol
  //public final static String RESPONSE_FAIL_LOWERCASE = "not specified by protocol";

  public final static String[] SUPPORTED_COMMANDS = new String[]{CMD_HELP, CMD_RANDOM, CMD_LOAD, CMD_INFO, CMD_BYE, CMD_CLEAR, CMD_LIST};

}
