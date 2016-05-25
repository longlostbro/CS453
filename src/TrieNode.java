import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by longl on 5/24/2016.
 */
public class TrieNode implements Comparable<TrieNode>
{
    private TrieNode[] children;
    private TrieNode parent;
    private boolean completed;
    private int queryFrequency = 0;
    private Map<String, Integer> modifiedTo;
    private char character;
    private double rankScore = 0;

    public TrieNode(TrieNode parent, char character)
    {
        this.parent = parent;
        children = new TrieNode[28];
        modifiedTo = new HashMap();
        this.character = character;
    }

    public TrieNode insert(String query)
    {
        if (query.length() > 0)
        {
            int index = query.charAt(0) - 'a';
            switch (index)
            {
                case -65:
                    index = 26;
                    break;
                case -58:
                    index = 27;
                    break;
            }
            if (children[index] == null)
            {
                children[index] = new TrieNode(this, query.charAt(0));
                return children[index].insert(query.substring(1));
            } else
                return children[index].insert(query.substring(1));

        } else
        {
            completed = true;
            ++queryFrequency;
            return this;
        }
    }

    public void addModification(String currentQuery)
    {
        if (modifiedTo.containsKey(currentQuery))
        {
            modifiedTo.put(currentQuery, modifiedTo.get(currentQuery) + 1);
        } else
        {
            modifiedTo.put(currentQuery, 1);
        }
    }

    public int getQueryFrequency()
    {
        return queryFrequency;
    }

    public TrieNode get(String query)
    {
        if (query.length() != 0)
        {
            int index = query.charAt(0) - 'a';
            switch (index)
            {
                case -65:
                    index = 26;
                    break;
                case -58:
                    index = 27;
                    break;
            }
            if (children[index] != null)
                return children[index].get(query.substring(1));
            else
                return null;
        }
        return this;
    }

    @Override
    public String toString()
    {
        if (parent != null)
            return parent.toString() + character;
        return "";
    }

    public TrieNode[] getCompletedChildren()
    {
        ArrayList<TrieNode> completedNodes = new ArrayList<>();
        for (TrieNode child : children)
        {
            if (child != null)
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrieNode trieNode = (TrieNode) o;

        if (completed != trieNode.completed) return false;
        if (queryFrequency != trieNode.queryFrequency) return false;
        if (character != trieNode.character) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(children, trieNode.children)) return false;
        if (parent != null ? !parent.equals(trieNode.parent) : trieNode.parent != null) return false;
        return modifiedTo != null ? modifiedTo.equals(trieNode.modifiedTo) : trieNode.modifiedTo == null;

    }

    @Override
    public int hashCode()
    {
        int result = Arrays.hashCode(children);
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (completed ? 1 : 0);
        result = 31 * result + queryFrequency;
        result = 31 * result + (modifiedTo != null ? modifiedTo.hashCode() : 0);
        result = 31 * result + (int) character;
        return result;
    }


    @Override
    public int compareTo(TrieNode o)
    {
        if (this.queryFrequency > o.queryFrequency)
            return -1;
        else if (this.queryFrequency < o.queryFrequency)
            return 1;
        return 0;
    }

    public String toStringAndScore()
    {
        return String.format("%s\t%d", toString(), queryFrequency);
    }

    public Map<String, Integer> getModifiedTo()
    {
        return modifiedTo;
    }

    public void setRankScore(double score)
    {
        this.rankScore = score;
    }

    public double getRankScore()
    {
        return rankScore;
    }
}
