import java.util.HashMap;
import java.util.Map;

/**
 * Created by longl on 6/11/2016.
 */
public class ClassProbabilities
{
    Map<String, Double> classToProbability;
    public ClassProbabilities()
    {
        classToProbability = new HashMap<>();
    }

    public void add(String className, double probability)
    {
        if(classToProbability.containsKey(className))
        {
            //do nothing
        }
        else
        {
            classToProbability.put(className, probability);
        }
    }

}
