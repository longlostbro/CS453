import javafx.collections.transformation.SortedList;
import org.apache.commons.lang.time.StopWatch;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by longl on 6/11/2016.
 */
public class MNBProbability
{
    int classCount = 0;
    Map<String, DocumentClass> classes;
    ClassProbabilities classProbabilities;
    WordProbabilities wordProbabilities;
    Map<String, Integer> wordFrequency;
    Set<String> uniqueWords;

    public MNBProbability(String path_to_folder)
    {
        uniqueWords = new HashSet<>();
        wordFrequency = new HashMap<>();
        StopWatch watch = new StopWatch();
        watch.start();
        classes = new HashMap<>();
        File folder = new File(path_to_folder);
        for (final File classification : folder.listFiles())
        {
            classCount++;
            DocumentClass documentClass = new DocumentClass(classification);
            classes.put(documentClass.getClassName(), documentClass);
            Set<String> classUniqueWords = documentClass.getUniqueWords();
            uniqueWords.addAll(classUniqueWords);
            for (String word : classUniqueWords)
            {
                if (wordFrequency.containsKey(word))
                {
                    wordFrequency.put(word, wordFrequency.get(word) + documentClass.getWordFrequency(word));
                } else
                {
                    wordFrequency.put(word, documentClass.getWordFrequency(word));
                }
            }
        }
        watch.split();
        System.out.println("Finished indexing: " + watch.toSplitString());
        wordProbabilities = computeWordProbability(classes);
        watch.split();
        System.out.println("Finished computing word probability: " + watch.toSplitString());
        classProbabilities = computeClassProbability(classes);
        watch.split();
        System.out.println("Finished computing class probability: " + watch.toSplitString());
        List<Document> testSet = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (DocumentClass documentClass : classes.values())
        {
            for (Document doc : documentClass.getUnclassifiedSet())
            {
                testSet.add(doc);
                labels.add(label(doc));
            }
        }
        watch.split();
        System.out.println("Finished labeling: " + watch.toSplitString() );
        System.out.println("Accuracy: "+MNBEvaluation.accuracyMeasure(testSet, labels));
        System.out.println("Finished labeling: " + watch.toSplitString() );
    }

    public double classProbability(String className)
    {
        if (classes.containsKey(className))
        {
            DocumentClass documentClass = classes.get(className);
            double numDocsInC = documentClass.getDocumentCount();
            double numDocuments = getTotalDocumentCount();
            return numDocsInC / numDocuments;
        }
        return 0;
    }

    public double wordProbability(String word)
    {
        double numDocsWithWord = 0;
        double totalDocumentCount = 0;
        for (DocumentClass documentClass : classes.values())
        {
            numDocsWithWord += documentClass.getNumberOfDocumentsWithWord(word);
            totalDocumentCount += documentClass.getDocumentCount();
        }
        return numDocsWithWord / totalDocumentCount;
    }

    public double notWordProbability(String word)
    {
        double numDocsWithoutWord = 0;
        double totalDocumentCount = 0;
        for (DocumentClass documentClass : classes.values())
        {
            numDocsWithoutWord += documentClass.getNumberOfDocumentsWithoutWord(word);
            totalDocumentCount += documentClass.getDocumentCount();
        }
        return numDocsWithoutWord / totalDocumentCount;
    }

    public double probabilityOfClassGivenWord(String className, String word)
    {
        double numDocsWithWordOfClass = 0;
        if (classes.containsKey(className))
        {
            numDocsWithWordOfClass = classes.get(className).getNumberOfDocumentsWithWord(word);
        }
        double numDocsWithWord = 0;
        for (DocumentClass documentClass : classes.values())
        {
            numDocsWithWord += documentClass.getNumberOfDocumentsWithWord(word);
        }
        if (numDocsWithWord != 0)
        {
            return numDocsWithWordOfClass / numDocsWithWord;
        }
        return 0;
    }

    public double probabilityOfClassGivenNotWord(String className, String word)
    {
        double numDocsWithoutWordOfClass = 0;
        if (classes.containsKey(className))
        {
            numDocsWithoutWordOfClass = classes.get(className).getNumberOfDocumentsWithoutWord(word);
        }
        double numDocsWithoutWord = 0;
        for (DocumentClass documentClass : classes.values())
        {
            numDocsWithoutWord += documentClass.getNumberOfDocumentsWithoutWord(word);
        }
        if (numDocsWithoutWord != 0)
        {
            return numDocsWithoutWordOfClass / numDocsWithoutWord;
        }
        return 0;
    }

    public List<Word> featureSelection(Map<String, DocumentClass> trainingSet, int m)
    {
        List<Word> rankedWords = uniqueWords.stream().map(word -> new Word(word, IG(word))).collect(Collectors.toList());
        Collections.sort(rankedWords);
        return rankedWords.subList(0, m);
    }

    public double IG(String word)
    {
        double result = 0;
        double sumPcLogPc = 0;
        double sumPcWLogPcW = 0;
        double sumPcNWLogPcNW = 0;
        for (DocumentClass documentClass : classes.values())
        {
            String className = documentClass.getClassName();
            double probabilityOfClass = classProbability(className);
            sumPcLogPc -= probabilityOfClass * Math.log(probabilityOfClass);
            //System.out.println(String.format("%f",-probabilityOfClass*Math.log(probabilityOfClass)));
            //System.out.print(String.format("-%flog%f",probabilityOfClass,probabilityOfClass));
        }
        //System.out.println();
        for (DocumentClass documentClass : classes.values())
        {
            String className = documentClass.getClassName();
            double probOfClassGivenWord = probabilityOfClassGivenWord(className, word);
            double probOfClassGivenNotWord = probabilityOfClassGivenNotWord(className, word);
            sumPcWLogPcW += wordProbability(word) * probOfClassGivenWord * safeLog(probOfClassGivenWord);
            sumPcNWLogPcNW += notWordProbability(word) * probOfClassGivenNotWord * safeLog(probOfClassGivenNotWord);
            //System.out.print(String.format("+%f*%flog%f",wordProbability(word),probOfClassGivenWord,probOfClassGivenWord));
            //System.out.println();
            //System.out.println(String.format("+%f*%flog%f",notWordProbability(word),probOfClassGivenNotWord,probOfClassGivenNotWord));
        }
        result = sumPcLogPc + sumPcWLogPcW + sumPcNWLogPcNW;
        return result;
    }

    public Set<String> getUniqueWords()
    {
        return uniqueWords;
    }

    public Integer getWordFrequency(String word)
    {
        if (wordFrequency.containsKey(word))
            return wordFrequency.get(word);
        return 0;
    }

    public WordProbabilities computeWordProbability(Map<String, DocumentClass> trainingSet)
    {
        WordProbabilities probs = new WordProbabilities();
        Set<String> totalUniqueWords = getUniqueWords();
        for (String word : totalUniqueWords)
        {
            for (DocumentClass documentClass : trainingSet.values())
            {
                double frequencyInClass = getWordFrequency(word);
                double totalInClass = documentClass.getUniqueWords().size();
                double uniqueWordsTotal = totalUniqueWords.size();
                double probability = (frequencyInClass + 1.0) / (totalInClass + uniqueWordsTotal);
                probs.add(word, documentClass.getClassName(), probability);
            }
        }
        for (DocumentClass documentClass : trainingSet.values())
        {
            double totalInClass = documentClass.getUniqueWords().size();
            double uniqueWordsTotal = totalUniqueWords.size();
            probs.add("default", documentClass.getClassName(), (0 + 1) / (totalInClass + uniqueWordsTotal));
        }
        return probs;
    }

    public ClassProbabilities computeClassProbability(Map<String, DocumentClass> trainingSet)
    {
        double totalProb = 0;
        ClassProbabilities probs = new ClassProbabilities();
        for (DocumentClass documentClass : trainingSet.values())
        {
            double docsInClass = documentClass.getDocumentCount();
            double totaldocs = getTotalDocumentCount();
            double probability = (docsInClass / totaldocs);
            probs.add(documentClass.getClassName(), probability);
            totalProb+=probability;
        }
        return probs;
    }

    public double getWordProbability(String word, String className)
    {
        return wordProbabilities.getProbability(word, className);
    }

    public double getClassProbability(String className)
    {
        return classProbabilities.getProbability(className);
    }

    public String label(Document testDocument)
    {
        Set<String> testDocumentWords = testDocument.getWords();
        List<Label> labels = new ArrayList<>();
        for (DocumentClass documentClass : classes.values())
        {
            String className = documentClass.getClassName();
            double classProbability = getClassProbability(className);
            BigDecimal probability = new BigDecimal(classProbability);
            for (String word : testDocumentWords)
            {
                double wordProb = getWordProbability(word, className);
                probability = probability.multiply(new BigDecimal(wordProb));
            }
            labels.add(new Label(className, probability));
        }
        Collections.sort(labels);
        return labels.get(0).className;
    }

    private double safeLog(double value)
    {
        if (value != 0)
            return Math.log(value);
        else
            return 0;
    }

    public int getClassCount()
    {
        return classCount;
    }

    public int getTotalDocumentCount()
    {
        int count = 0;
        for (DocumentClass documentClass : classes.values())
        {
            count += documentClass.getDocumentCount();
        }
        return count;
    }
}
