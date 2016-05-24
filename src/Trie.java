/**
 * Created by longl on 5/24/2016.
 */
public class Trie {
    TrieNode rootNode;
    public Trie()
    {
        rootNode = new TrieNode();
    }
    public TrieNode insertQuery(String query)
    {
        return rootNode.insert(query);
    }
}
