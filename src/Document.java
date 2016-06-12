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
                            if (wordToCount.containsKey(word))
                            {
                                wordToCount.put(word, wordToCount.get(word) + 1);
                            } else
                            {
                                wordToCount.put(word, 1);
                            }
                        }
                    }
                }
            }

            /*BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()));
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
            }*/
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
