import org.apache.commons.lang.text.StrBuilder;

import java.io.File;
import java.util.*;

/**
 * Created by longl on 6/11/2016.
 */
public class DocumentClass
{
    private String className;
    Set<String> uniqueWords;
    private List<Document> unclassifiedSet;
    private List<Document> trainingSet;
    private int totalWordCount;

    public DocumentClass(File folder)
    {
        uniqueWords = new HashSet<>();
        trainingSet = new ArrayList<>();
        unclassifiedSet = new ArrayList<>();
        className = folder.getName();
        List<File> documentFiles = Arrays.asList(folder.listFiles());
        Collections.shuffle(documentFiles);
        for(int i = 0; i < documentFiles.size(); i++)
        {
            File doc = documentFiles.get(i);
            Document document = new Document(doc, className);
            if(i+1 < .8*documentFiles.size())
            {
                totalWordCount++;
                uniqueWords.addAll(document.getWords());
                trainingSet.add(document);
            }
            else
                unclassifiedSet.add(document);
        }
    }

    public List<Document> getTrainingSet()
    {
        return trainingSet;
    }
    public List<Document> getUnclassifiedSet()
    {
        return unclassifiedSet;
    }

    public int getDocumentCount()
    {
        return trainingSet.size();
    }

    public String getClassName()
    {
        return className;
    }

    public int getNumberOfDocumentsWithWord(String word)
    {
        int count = 0;
        for(Document doc : trainingSet)
        {
            if(doc.hasWord(word))
            {
                count++;
            }
        }
        return count;
    }
    public int getNumberOfDocumentsWithoutWord(String word)
    {
        int count = 0;
        for(Document doc : trainingSet)
        {
            if(!doc.hasWord(word))
            {
                count++;
            }
        }
        return count;
    }

    public Set<String> getUniqueWords()
    {
        return uniqueWords;
    }

    public int getWordFrequency(String word)
    {
        int count = 0;
        for(Document doc : trainingSet)
        {
            count += doc.getCountForWord(word);
        }
        return count;
    }

    public int getTotalWordCount()
    {
        return totalWordCount;
    }
}
