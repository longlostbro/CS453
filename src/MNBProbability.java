import javafx.collections.transformation.SortedList;

import java.io.File;
import java.util.*;

/**
 * Created by longl on 6/11/2016.
 */
public class MNBProbability
{
    int classCount = 0;
    Map<String, DocumentClass> classes;
    ClassProbabilities classProbabilities;
    public MNBProbability(String path_to_folder)
    {
        classes = new HashMap<>();
        File folder = new File(path_to_folder);
        for (final File classification : folder.listFiles())
        {
            classCount++;
            DocumentClass documentClass = new DocumentClass(classification);
            classes.put(documentClass.getClassName(), documentClass);
        }
    }

    public double classProbability(String className)
    {
        if(classes.containsKey(className))
        {
            DocumentClass documentClass = classes.get(className);
            double numDocsInC = documentClass.getDocumentCount();
            double numDocuments = getTotalDocumentCount();
            return numDocsInC/numDocuments;
        }
        return 0;
    }

    public double wordProbability(String word)
    {
        double numDocsWithWord = 0;
        double totalDocumentCount = 0;
        for(DocumentClass documentClass : classes.values())
        {
            numDocsWithWord += documentClass.getNumberOfDocumentsWithWord(word);
            totalDocumentCount += documentClass.getDocumentCount();
        }
        return numDocsWithWord/totalDocumentCount;
    }

    public double notWordProbability(String word)
    {
        double numDocsWithoutWord = 0;
        double totalDocumentCount = 0;
        for(DocumentClass documentClass : classes.values())
        {
            numDocsWithoutWord += documentClass.getNumberOfDocumentsWithoutWord(word);
            totalDocumentCount += documentClass.getDocumentCount();
        }
        return numDocsWithoutWord/totalDocumentCount;
    }

    public double probabilityOfClassGivenWord(String className, String word)
    {
        double numDocsWithWordOfClass = 0;
        if(classes.containsKey(className))
        {
            numDocsWithWordOfClass = classes.get(className).getNumberOfDocumentsWithWord(word);
        }
        double numDocsWithWord = 0;
        for(DocumentClass documentClass : classes.values())
        {
            numDocsWithWord += documentClass.getNumberOfDocumentsWithWord(word);
        }
        if(numDocsWithWord != 0)
        {
            return numDocsWithWordOfClass/numDocsWithWord;
        }
        return 0;
    }

    public double probabilityOfClassGivenNotWord(String className, String word)
    {
        double numDocsWithoutWordOfClass = 0;
        if(classes.containsKey(className))
        {
            numDocsWithoutWordOfClass = classes.get(className).getNumberOfDocumentsWithoutWord(word);
        }
        double numDocsWithoutWord = 0;
        for(DocumentClass documentClass : classes.values())
        {
            numDocsWithoutWord += documentClass.getNumberOfDocumentsWithoutWord(word);
        }
        if(numDocsWithoutWord != 0)
        {
            return numDocsWithoutWordOfClass/numDocsWithoutWord;
        }
        return 0;
    }

    public List<Word> featureSelection(Map<String,DocumentClass> trainingSet, int m)
    {
        List<Word> rankedWords = new ArrayList<Word>();
        for(DocumentClass documentClass : trainingSet.values())
        {
            for(Document document : documentClass.getTrainingSet())
            {
                for(String word : document.getWords())
                {
                    rankedWords.add(new Word(word, IG(word)));
                }
            }
        }
        Collections.sort(rankedWords);
        return rankedWords.subList(0,m);
    }

    public double IG(String word)
    {
        double result = 0;
        double sumPcLogPc = 0;
        double sumPcWLogPcW = 0;
        double sumPcNWLogPcNW = 0;
        for(DocumentClass documentClass : classes.values())
        {
            String className = documentClass.getClassName();
            double probabilityOfClass = classProbability(className);
            sumPcLogPc -= probabilityOfClass*Math.log(probabilityOfClass);
            //System.out.println(String.format("%f",-probabilityOfClass*Math.log(probabilityOfClass)));
            //System.out.print(String.format("-%flog%f",probabilityOfClass,probabilityOfClass));
        }
        System.out.println();
        for(DocumentClass documentClass : classes.values())
        {
            String className = documentClass.getClassName();
            double probOfClassGivenWord = probabilityOfClassGivenWord(className, word);
            double probOfClassGivenNotWord = probabilityOfClassGivenNotWord(className, word);
            sumPcWLogPcW += wordProbability(word)*probOfClassGivenWord*safeLog(probOfClassGivenWord);
            sumPcNWLogPcNW += notWordProbability(word)*probOfClassGivenNotWord*safeLog(probOfClassGivenNotWord);
            //System.out.print(String.format("+%f*%flog%f",wordProbability(word),probOfClassGivenWord,probOfClassGivenWord));
            //System.out.println();
            //System.out.println(String.format("+%f*%flog%f",notWordProbability(word),probOfClassGivenNotWord,probOfClassGivenNotWord));
        }
        result = sumPcLogPc+sumPcWLogPcW+sumPcNWLogPcNW;
        return result;
    }

    public Set<String> getUniqueWords(Map<String, DocumentClass> trainingSet)
    {
        Set<String> uniqueWords = new HashSet<>();
        for(DocumentClass docClass : trainingSet.values())
        {
            uniqueWords.addAll(docClass.getUniqueWords());
        }
        return uniqueWords;
    }

    public WordProbabilities computeWordProbability(Map<String, DocumentClass> trainingSet)
    {
        WordProbabilities probs = new WordProbabilities();
        for(DocumentClass documentClass : trainingSet.values())
        {
            Set<String> totalUniqueWords = getUniqueWords(trainingSet);
            for(String word : totalUniqueWords)
            {
                double frequencyInClass = documentClass.getWordFrequency(word);
                double totalInClass = documentClass.getUniqueWords().size();
                double uniqueWordsTotal = totalUniqueWords.size();
                probs.add(word, documentClass.getClassName(), (frequencyInClass+1)/(totalInClass+uniqueWordsTotal));
            }
        }
        return probs;
    }

    public ClassProbabilities computeClassProbability(Map<String, DocumentClass> trainingSet)
    {
        ClassProbabilities probs = new ClassProbabilities();
        for(DocumentClass documentClass : trainingSet.values())
        {
                double docsInClass = documentClass.getDocumentCount();
                double totaldocs = getTotalDocumentCount();
                probs.add(documentClass.getClassName(), (docsInClass/totaldocs));
        }
        return probs;
    }

    public WordProbabilities getWordProbability(String word, String className)
    {

    }

    private double safeLog(double value)
    {
        if(value != 0)
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
        for(DocumentClass documentClass : classes.values())
        {
            count += documentClass.getDocumentCount();
        }
        return count;
    }
}
