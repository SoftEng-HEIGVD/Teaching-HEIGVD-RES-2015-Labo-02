package ch.heigvd.res.labs.roulette.net.protocol;

/**
 * This class is used to serialize/deserialize the response sent by the server
 * when processing the "BYE" command defined in the protocol specification. The
 * JsonObjectMapper utility class can use this class.
 *
 * @author Kevin Moreira
 */
public class ByeCommandResponse
{
    private String status;
    private int nbrCommands;

    public ByeCommandResponse(String status, int nbrCommands)
    {
        this.status = status;
        this.nbrCommands = nbrCommands;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setNumberOfCommands(int nbrCommands)
    {
        this.nbrCommands = nbrCommands;
    }

    public int getNumberOfCommands()
    {
        return nbrCommands;
    }
}