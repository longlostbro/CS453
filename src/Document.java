import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by longl on 6/11/2016.
 */
public class Document
{
    private String classification;
    private String name;
    private HashMap<String, Integer> wordToCount;
    private int totalWordCount = 0;

    public Set<String> getWords()
    {
        return wordToCount.keySet();
    }

    public Document(File file, String classification)
    {
        name = file.getName();
        this.classification = classification;
        wordToCount = new HashMap<>();
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            StringBuilder everything = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null)
            {
                everything.append(line + " ");
            }
            for(String word : Arrays.asList(everything.toString().replaceAll(System.getProperty("line.separator"), " ").replaceAll("[^a-zA-Z ]", "").toLowerCase().replaceAll("\\p{Punct}+", "").trim().split("\\s+")))
            {
                totalWordCount++;
                if(wordToCount.containsKey(word))
                {
                    wordToCount.put(word,wordToCount.get(word)+1);
                }
                else
                {
                    wordToCount.put(word,1);
                }
            }
        }
        catch (Exception e)
        {
            System.err.println(e);
        }
    }

    public String getName()
    {
        return name;
    }

    public int getCountForWord(String word)
    {
        if(wordToCount.containsKey(word))
        {
            return wordToCount.get(word);
        }
        return 0;
    }

    public int getTotalWordCount()
    {
        return totalWordCount;
    }

    public int getUniqueWordCount()
    {
        return wordToCount.keySet().size();
    }

    public String getClassification()
    {
        return classification;
    }

    public boolean hasWord(String word)
    {
        return wordToCount.containsKey(word);
    }
}
