import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by longl on 6/11/2016.
 */
public class WordProbabilities
{
    HashMap<String, List<Pair<String, Double>>> wordToClassToProbability = new HashMap<>();

    public WordProbabilities()
    {
    }

    public void add(String word, String className, double probability)
    {
        if(wordToClassToProbability.containsKey(word))
        {
            wordToClassToProbability.get(word).add(new Pair<>(className, probability));
        }
        else
        {
            List<Pair<String, Double>> list = new ArrayList<>();
            list.add(new Pair<>(className, probability));
            wordToClassToProbability.put(word,list);
        }
    }
}
