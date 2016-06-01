import java.io.*;
import java.util.*;

/**
 * Created by dtaylor on 5/31/2016.
 */
public class NoisyChannel
{

    LogManager logs;
    public NoisyChannel()
    {

        logs = new LogManager();
    }

    public void parseLogFile(File file) {

        try
        {
            BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            StringBuilder everything = new StringBuilder();
            String line;
            in.readLine();
            while( (line = in.readLine()) != null) {
                logs.addSessionFromLine(line);
            }
            logs.findCorrections();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public double getErrorProbability(String word, String incorrectWord)
    {
        return logs.getErrorProbability(word,incorrectWord);
    }
}
