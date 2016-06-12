import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by longl on 6/11/2016.
 */
public class WordProbabilities
{
    HashMap<String, Map<String, Double>> wordToClassToProbability = new HashMap<>();

    public WordProbabilities()
    {
    }

    public void add(String word, String className, double probability)
    {
        if(probability == 0)
            System.out.println("error");
        if(wordToClassToProbability.containsKey(word))
        {
            wordToClassToProbability.get(word).put(className, probability);
        }
        else
        {
            Map<String, Double> map = new HashMap<>();
            map.put(className, probability);
            wordToClassToProbability.put(word,map);
        }
    }

    public double getProbability(String word, String className)
    {
        double value = wordToClassToProbability.get("default").get(className);
        if(wordToClassToProbability.containsKey(word))
        {
            if(wordToClassToProbability.get(word).containsKey(className))
            {
                value = wordToClassToProbability.get(word).get(className);
            }
            else
            {
                value = wordToClassToProbability.get(word).get("default");
            }
        }
        if(value == 0)
            System.out.println("error");
        return value;
    }
}
