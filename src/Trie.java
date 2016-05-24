/**
 * Created by longl on 5/24/2016.
 */
public class Trie {
    TrieNode rootNode;
    public Trie()
    {
        rootNode = new TrieNode(null, '#');
    }
    public TrieNode insertQuery(String query)
    {
        return rootNode.insert(query);
    }

    public TrieNode getNode(String query) {
        return rootNode.get(query);
    }
}
