import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by longl on 6/11/2016.
 */
public class Document
{
    private String classification;
    private String name;
    public Set<String> words = new HashSet<>();
    private int totalWordCount = 0;


    public Set<String> getWords()
    {
        return words;
    }

    public Document(File file, String classification)
    {
        name = file.getName();
        this.classification = classification;
        try
        {
            PorterStemmer stemmer = new PorterStemmer();
            Scanner inScanner=new Scanner(new FileReader(file));
            inScanner.useDelimiter("\n\n");
            inScanner.next();
            List<String> stopwords = Files.readAllLines(Paths.get("Resources/stopwords.txt"));
            String domainPattern = "[a-z0-9\\-\\.]+\\.(com|org|net|mil|edu|(co\\.[a-z].))";
            Pattern pFind = Pattern.compile(domainPattern);

            while (inScanner.hasNextLine())
            {
                String str = inScanner.nextLine().toLowerCase();
                if(pFind.matcher(str).find()==false)
                {
                    str = str.replaceAll("[^a-zA-Z]", " ");
                    StringTokenizer token = new StringTokenizer(str, " \t\n\r\f\",.:;?![]>-()/|'");
                    while(token.hasMoreTokens())
                    {
                        String word = token.nextToken();
                        if(!stopwords.contains(word))
                        {
                            if(word.length() > 2)
                                word = stemmer.stem(word);
                            totalWordCount++;
                            words.add(word);
                        }
                    }
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

    public int getTotalWordCount()
    {
        return totalWordCount;
    }

    public int getUniqueWordCount()
    {
        return words.size();
    }

    public String getClassification()
    {
        return classification;
    }

    public boolean hasWord(String word)
    {
        return words.contains(word);
    }
}
