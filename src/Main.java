import org.apache.commons.codec.language.Soundex;

import javax.swing.*;
import java.io.File;

/**
 * Created by longl on 5/24/2016.
 */
public class Main {
    public static void main(String[] args) {
        Soundexer soundexer = new Soundexer();
        soundexer.addCorrectWordsFromFile("dictionary.txt");
        NoisyChannel noisy = new NoisyChannel();
        noisy.parseLogFile(new File("query_log.txt"));
        //soundexer.getPossibleWords(word));
        //soundexer.calculateEditDistance("cat","catt");
    }
}
