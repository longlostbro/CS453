import java.util.List;

/**
 * Created by longl on 6/13/2016.
 */
public class MNBEvaluation
{
    public MNBEvaluation()
    {

    }

    public static double accuracyMeasure(List<Document> testSet, List<String> labels)
    {
        double correctCount = 0;
        double incorrectCount = 0;
        double totalCount = testSet.size();
        for(int i = 0; i < testSet.size(); i++)
        {
            Document doc = testSet.get(i);
            String label = labels.get(i);
            if(doc.getClassification().equals(label))
            {
                correctCount++;
            }
            else
            {
                incorrectCount++;
            }
        }
        double accuracy = correctCount/totalCount;
        return accuracy;
    }
}
