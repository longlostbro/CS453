import org.apache.commons.codec.language.Soundex;

import javax.swing.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by longl on 5/24/2016.
 */
public class Main {
    public static void main(String[] args) {
        Soundexer soundexer = new Soundexer();
        soundexer.addCorrectWordsFromFile("dictionary.txt");
        NoisyChannel noisy = new NoisyChannel();
        noisy.parseLogFile(new File("query_log.txt"));
        DocumentIndex index = new DocumentIndex();
        index.indexDocuments("wiki/To_be_posted");
        Scanner scanner = new Scanner(System.in);
        for(;;)
        {
            String badQuery = scanner.nextLine();
            StringBuilder correctedQuery = new StringBuilder();
            String soundexCode = null;
            List<String> possibleWords = null;

            for (String badWord : badQuery.toLowerCase().split("\\s"))
            {
                if (!Soundexer.Instance().isValid(badWord))
                {
                    String bestWord = null;
                    double bestScore = 0;
                    possibleWords = soundexer.getPossibleWords(badWord);
                    for (String word : possibleWords)
                    {
                        double score = noisy.getErrorProbability(word, badWord) * index.probabilityW(word);
                        if (score > bestScore)
                        {
                            bestWord = word;
                            bestScore = score;
                        }
                    }
                    if (bestWord != null)
                    {
                        correctedQuery.append(bestWord + " ");/* + " - "+bestScore);*/
                        soundexCode = soundexer.getCode(bestWord);
                    } else
                        correctedQuery.append("ERROR ");
                } else
                {
                    correctedQuery.append(badWord + " ");
                }

            }
            System.out.println(String.format("Original Query: %s\tCorrected Query: %s", badQuery, correctedQuery.toString()));
            System.out.println(String.format("Soundex Code: %s", soundexCode));
            System.out.println(String.format("Suggested Corrections: %s", Arrays.toString(possibleWords.toArray()).replaceAll("\\[", "").replaceAll("\\]", "")));
            index.query(correctedQuery.toString());
        }
        //index.query("killing incident");
        /*index.query("killing incident");
        index.query("suspect charged with murder\n");
        index.query("court");
        index.query("jury sentenced murderer to prison\n");
        index.query("movie");
        index.query("entertainment films\n");
        index.query("court appeal");
        index.query("action film producer");
        index.query("drunk driving accusations");
        index.query("actor appeared in movie premiere");*/
        //soundexer.getPossibleWords(word));
        //soundexer.calculateEditDistance("cat","catt");
    }
}
