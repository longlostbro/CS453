import org.apache.commons.lang.time.StopWatch;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by longl on 5/24/2016.
 */
public class Main
{
    public static void main(String[] args)
    {
        try
        {


            FileWriter fw = new FileWriter("output2.txt");
            List<Integer> tests = new ArrayList<>();
            tests.add(6200);
            tests.add(12400);
            tests.add(18600);
            tests.add(24800);
            StopWatch watch = new StopWatch();
            for(int i = 0; i < 5; i++)
            {
                for (int m : tests)
                {
                    //TRAIN
                    watch.start();
                    MNBProbability manager = new MNBProbability("Resources/20NG");
                    //System.out.println("Finished indexing");

                    watch.stop();
                    fw.append("Training Time: " + (watch.getTime() / 1000.0) + "s\n");
                    fw.append("M: " + m + "\n");
                    watch.reset();
                    watch.start();
                    List<Word> words = manager.featureSelection(manager.classes, m);
                    List<String> wordsToRemove = new ArrayList<String>(manager.uniqueWords);
                    for (Word word : words)
                    {
                        wordsToRemove.remove(word);
                    }
                    for(String word : wordsToRemove)
                    {
                        manager.wordFrequency.remove(word);
                        for (DocumentClass docClass : manager.classes.values())
                        {
                            docClass.uniqueWords.remove(word);
                            for (Document doc : docClass.getTrainingSet())
                            {
                                doc.wordToCount.remove(word);
                            }
                        }
                    }

                    manager.wordProbabilities = manager.computeWordProbability(manager.classes);
                    //System.out.println("Finished computing word probability");

                    manager.classProbabilities = manager.computeClassProbability(manager.classes);

                    watch.stop();
                    fw.append("Feature Selection Time: " + (watch.getTime() / 1000.0) + "s\n");

                    //TEST!!!
                    watch.reset();
                    watch.start();
                    List<Document> testSet = new ArrayList<>();
                    List<String> labels = new ArrayList<>();
                    for (DocumentClass documentClass : manager.classes.values())
                    {
                        for (Document doc : documentClass.getUnclassifiedSet())
                        {
                            testSet.add(doc);
                            labels.add(manager.label(doc));
                        }
                    }
                    watch.stop();
                    //System.out.println("Finished labeling: ");
                    double accuracy = MNBEvaluation.accuracyMeasure(testSet, labels);
                    fw.append("Accuracy: " + accuracy + "\n");
                    fw.append("Testing Time: " + (watch.getTime() / 1000.0) + "s\n");
                    watch.reset();
                    fw.append("\n");
                    fw.flush();
                    System.out.println("Set Complete");
                }
            }
            fw.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        //MNBProbability manager = new MNBProbability("Resources/Test");
        //System.out.println(manager.IG("cheap"));
        //System.out.println(String.format("cheap: %f\nbuy: %f\nbanking: %f\ndinner: %f\nthe: %f",manager.IG("cheap"),manager.IG("buy"),manager.IG("banking"),manager.IG("dinner"),manager.IG("the")));
    }
}
/*
Number of documents in class
Number of documents in training set
Number of docs with word w
Number of docs without word w
Number of docs with word w in class c
 */