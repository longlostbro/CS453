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
    public Map<String, Integer> wordToDocumentCount = new HashMap<>();

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
                for(String word : document.getWords())
                {
                    if(wordToDocumentCount.containsKey(word))
                    {
                        wordToDocumentCount.put(word,wordToDocumentCount.get(word)+1);
                    }
                    else
                    {
                        wordToDocumentCount.put(word,1);
                    }
                }
            }
            else
                unclassifiedSet.add(document);
        }
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
        if(wordToDocumentCount.containsKey(word))
            return wordToDocumentCount.get(word);
        return 0;
    }
    public int getNumberOfDocumentsWithoutWord(String word)
    {
        return getDocumentCount()-getNumberOfDocumentsWithWord(word);
    }

    public Set<String> getUniqueWords()
    {
        return uniqueWords;
    }

    public int getTotalWordCount()
    {
        return totalWordCount;
    }
}
