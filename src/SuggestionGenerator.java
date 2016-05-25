import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;

/**
 * Created by longl on 5/24/2016.
 */
public class SuggestionGenerator {
    private final Trie trie;
    StopWords sw;

    public SuggestionGenerator(Trie trie)
    {
        this.trie = trie;
        sw = new StopWords();
    }

    public String generateSuggestions(String query)
    {
        query = removeBeginningStopWords(query);
        if(query.length() > 0)
        {
            TrieNode node = trie.getNode(query);
            if (node != null)
            {
                TrieNode[] suggestions = node.getCompletedChildren();
                if (suggestions.length > 0)
                {
                    Arrays.sort(suggestions,new NodeTrieComparator(query, trie));
                    StringBuilder str = new StringBuilder();
                    for (int i = 0; i < Math.min(8, suggestions.length); i++)
                    {
                        str.append(String.format("%s %f\n",suggestions[i].toString(),suggestions[i].getRankScore()));
                    }
                    return str.toString();
                } else
                {
                    return "No Suggestions Found";
                }
            }
        }
        return "None found";
    }

    public String removeBeginningStopWords(String query)
    {
        if(query.contains(" ") && sw.contains(query.split(" ")[0]))
        {
            return removeBeginningStopWords(query.substring(query.indexOf(" ")+1));
        }
        else
        {
            if(sw.contains(query))
            {
                return "";
            }
        }
        return query;
    }


}
