/**
 * Created by longl on 5/24/2016.
 */
public class SuggestionGenerator {
    private final Trie trie;

    public SuggestionGenerator(Trie trie)
    {
        this.trie = trie;
    }

    public String generateSuggestions(String query)
    {
        TrieNode node = trie.getNode(query);
        if(node != null)
        {
            TrieNode[] suggestions = node.getCompletedChildren();
            if(suggestions.length >0)
            {
                StringBuilder str = new StringBuilder();
                for(TrieNode suggestion : suggestions)
                {
                    str.append(suggestion.toString()+"\n");
                }
                return str.toString();
            }
            else
            {
                return "No Suggestions Found";
            }
        }
        return "None found";
    }
}
