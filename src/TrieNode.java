import java.util.HashMap;
import java.util.Map;

/**
 * Created by longl on 5/24/2016.
 */
public class TrieNode {
    private TrieNode[] children;
    private TrieNode parent;
    private boolean completed;
    private int queryFrequency = 0;
    private Map<String, Integer> modifiedTo;

    public TrieNode()
    {
        children = new TrieNode[28];
        modifiedTo = new HashMap();
    }

    public TrieNode insert(String query) {
        if(query.length() > 0) {
            int index = query.charAt(0) - 'a';
            switch (index) {
                case -65:
                    index = 26;
                    break;
                case -58:
                    index = 27;
                    break;
            }
            if (children[index] == null) {
                children[index] = new TrieNode();
                return children[index].insert(query.substring(1));
            }
            else
                return children[index].insert(query.substring(1));

        }
        else
        {
            completed = true;
            ++queryFrequency;
            return this;
        }
    }

    public void addModification(String currentQuery) {
        if(modifiedTo.containsKey(currentQuery))
        {
            modifiedTo.put(currentQuery,modifiedTo.get(currentQuery)+1);
        }
        else
        {
            modifiedTo.put(currentQuery,1);
        }
    }

    public int getQueryFrequency()
    {
        return queryFrequency;
    }
}
