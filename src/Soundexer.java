import org.apache.commons.codec.language.Soundex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Created by dtaylor on 5/31/2016.
 */
public class Soundexer
{
    private static Soundexer _instance;
    public static Soundexer Instance()
    {
        if(_instance == null)
            _instance = new Soundexer();
        return _instance;
    }
    private Soundex soundex;
    public Map<String, Set<String>> codeToCorrectWordList;
    public Soundexer()
    {
        soundex = new Soundex();
        codeToCorrectWordList = new HashMap<>();
        _instance = this;
    }

    public void addCorrectWordsFromFile(String path)
    {
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(new File(path)));
            StringBuilder str = new StringBuilder();
            String nextLine;
            while ((nextLine = in.readLine()) != null)
            {
                String code = soundex.encode(nextLine);
                if (codeToCorrectWordList.containsKey(code))
                {
                    codeToCorrectWordList.get(code).add(nextLine);
                } else
                {
                    HashSet<String> set = new HashSet<>();
                    set.add(nextLine);
                    codeToCorrectWordList.put(code, set);
                }
            }
            in.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public List<String> getPossibleWords(String badWord)
    {
        List<String> possibleWords = new ArrayList<>();
        String code = soundex.encode(badWord);
        if(codeToCorrectWordList.containsKey(code))
        for(String word : codeToCorrectWordList.get(code))
        {
            if(editDistance(badWord, word) <=2)
                possibleWords.add(word);
        }
        return possibleWords;
    }

    public boolean isValid(String word)
    {
        String code = soundex.encode(word);
        if(codeToCorrectWordList.containsKey(code))
            return codeToCorrectWordList.get(code).contains(word);
        return false;
    }

    public static int editDistance(String word1, String word2) {
        int len1 = word1.length();
        int len2 = word2.length();

        // len1+1, len2+1, because finally return table[len1][len2]
        int[][] table = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            table[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            table[0][j] = j;
        }

        //iterate though, and check last char
        for (int i = 0; i < len1; i++) {
            char c1 = word1.charAt(i);
            for (int j = 0; j < len2; j++) {
                char c2 = word2.charAt(j);

                //if last two chars equal
                if (c1 == c2) {
                    //update table value for +1 length
                    table[i + 1][j + 1] = table[i][j];
                } else {
                    int replace = table[i][j] + 1;
                    int insert = table[i][j + 1] + 1;
                    int delete = table[i + 1][j] + 1;

                    int min = replace > insert ? insert : replace;
                    min = delete > min ? min : delete;
                    table[i + 1][j + 1] = min;
                }
            }
        }

        return table[len1][len2];
    }
}
