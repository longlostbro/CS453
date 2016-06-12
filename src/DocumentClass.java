import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by longl on 6/11/2016.
 */
public class DocumentClass
{
    private String className;
    private List<Document> unclassifiedSet;
    private List<Document> trainingSet;
    public DocumentClass(File folder)
    {
        trainingSet = new ArrayList<>();
        unclassifiedSet = new ArrayList<>();
        className = folder.getName();
        File[] documentFiles = folder.listFiles();
        for(int i = 0; i < documentFiles.length; i++)
        {
            File doc = documentFiles[i];
            if(i < .8*documentFiles.length)
                trainingSet.add(new Document(doc, className));
            else
                unclassifiedSet.add(new Document(doc, className));
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
        Set<String> uniqueWords = new HashSet<>();
        for(Document doc : trainingSet)
        {
            uniqueWords.addAll(doc.getWords());
        }
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
}
