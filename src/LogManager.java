import javafx.util.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dtaylor on 5/31/2016.
 */
public class LogManager
{
    Map<String, Map<String, Double>> correctWordToIncorrectWords;
    Map<String, Map<String, Double>> incorrectWordToCorrectWords;
    Map<Double, List<String[]>> sessions;

    public LogManager()
    {
        sessions = new HashMap<>();
        correctWordToIncorrectWords = new HashMap<>();
        incorrectWordToCorrectWords = new HashMap<>();
    }

    public void addSessionFromLine(String line)
    {
        String[] splitLine = line.split("\\t");
        double id = Double.parseDouble(splitLine[0]);
        if (sessions.containsKey(id))
        {
            sessions.get(id).add(splitLine[1].split("\\s"));
        }
        else
        {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(splitLine[1].split("\\s"));
            sessions.put(id,list);
        }
    }

    public void findCorrections()
    {
        for (List<String[]> queries : sessions.values())
        {
            for (String[] query : queries)
            {
                for (String[] comparisonQuery : queries)
                {
                    if(comparisonQuery != query)
                        checkForCorrection(query, comparisonQuery);
                }
            }
        }
    }

    public void checkForCorrection(String[] query1, String[] query2)
    {
        if (query1.length == query2.length)
        {
            int incorrectWordCount = 0;
            Pair<String, String> correction = null;
            for (int i = 0; i < query1.length; i++)
            {
                //if this is the word being corrected
                if (!isInDictionary(query1[i]) && isInDictionary(query2[i]))
                {
                    correction = new Pair<>(query1[i], query2[i]);
                    incorrectWordCount++;
                    if (incorrectWordCount > 1)
                        break;
                } else if (!query1[i].equals(query2[i]))
                {
                    break;
                }
            }
            if (incorrectWordCount == 1 && correction != null && withinEditDistance(correction.getKey(),correction.getValue()))
            {
                if (correctWordToIncorrectWords.containsKey(correction.getValue()))
                {
                    Map<String, Double> map = correctWordToIncorrectWords.get(correction.getValue());
                    if (map.containsKey(correction.getKey()))
                    {
                        double value = map.get(correction.getKey());
                        map.put(correction.getKey(), value + 1);
                    } else
                    {
                        map.put(correction.getKey(), 1.0);
                    }
                } else
                {
                    Map<String, Double> map = new HashMap<>();
                    map.put(correction.getKey(), 1.0);
                    correctWordToIncorrectWords.put(correction.getValue(), map);
                }
                if (incorrectWordToCorrectWords.containsKey(correction.getKey()))
                {
                    Map<String, Double> map = incorrectWordToCorrectWords.get(correction.getKey());
                    if (map.containsKey(correction.getValue()))
                    {
                        double value = map.get(correction.getValue());
                        map.put(correction.getValue(), value + 1);
                    } else
                    {
                        map.put(correction.getValue(), 1.0);
                    }
                } else
                {
                    Map<String, Double> map = new HashMap<>();
                    map.put(correction.getValue(), 1.0);
                    incorrectWordToCorrectWords.put(correction.getKey(), map);
                }
            }
        }
    }

    private boolean isInDictionary(String word)
    {
        return Soundexer.Instance().isValid(word);
    }

    private boolean withinEditDistance(String word1, String word2)
    {
        return Soundexer.editDistance(word1,word2) <= 2;
    }

    public double getErrorProbability(String correctWord, String incorrectWord)
    {
        double numerator = 0;
        if(incorrectWordToCorrectWords.containsKey(incorrectWord))
        {
            Map<String,Double> count = incorrectWordToCorrectWords.get(incorrectWord);
            if(count.containsKey(correctWord))
                numerator = count.get(correctWord);
        }
        double denominator = 1;
        if(correctWordToIncorrectWords.containsKey(correctWord))
        {
            Map<String,Double> count = correctWordToIncorrectWords.get(correctWord);
            denominator = 0;
            for(double value : correctWordToIncorrectWords.get(correctWord).values())
                denominator+=value;
        }
        if(denominator == 0)
            return 0;
        return numerator/denominator;
    }
}
