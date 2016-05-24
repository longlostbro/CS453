import javax.swing.*;

/**
 * Created by longl on 5/24/2016.
 */
public class Main {
    public static void main(String[] args) {
        QueryLogParser parser = new QueryLogParser("AOL-Clean-Data");
        parser.parse();
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                new QueryGui(new SuggestionGenerator(parser.getTrie()));
            }
        });
    }
}
