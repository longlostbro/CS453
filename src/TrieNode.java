import java.util.ArrayList;
import java.util.Arrays;
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
    private char character;

    public TrieNode(TrieNode parent, char character)
    {
        this.parent = parent;
        children = new TrieNode[28];
        modifiedTo = new HashMap();
        this.character = character;
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
                children[index] = new TrieNode(this, query.charAt(0));
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

    public TrieNode get(String query) {
        if(query.length() != 0)
        {
            int index = query.charAt(0) - 'a';
            switch (index) {
                case -65:
                    index = 26;
                    break;
                case -58:
                    index = 27;
                    break;
            }
            if(children[index] != null)
                return children[index].get(query.substring(1));
            else
                return null;
        }
        return this;
    }

    @Override
    public String toString() {
        if(parent != null)
            return parent.toString()+character;
        return "";
    }

    public TrieNode[] getCompletedChildren()
    {
        ArrayList<TrieNode> completedNodes = new ArrayList<>();
        for(TrieNode child : children)
        {
            if(child != null)
            {
                if (child.completed)
                {
                    completedNodes.add(child);
                }
                completedNodes.addAll(Arrays.asList(child.getCompletedChildren()));
            }
        }
        return completedNodes.toArray(new TrieNode[0]);
    }
}
